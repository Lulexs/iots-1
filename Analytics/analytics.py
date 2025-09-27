import os
import json
import logging
from logging.handlers import RotatingFileHandler
import paho.mqtt.client as mqtt
import requests
import asyncio
import threading
import nats

LOG_FILE = "analytics.log"
handler = RotatingFileHandler(LOG_FILE, maxBytes=5 * 1024 * 1024, backupCount=3)
formatter = logging.Formatter(
    fmt="%(asctime)s %(levelname)s %(name)s %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
handler.setFormatter(formatter)

logger = logging.getLogger("analytics")
logger.setLevel(logging.INFO)
logger.addHandler(handler)

BROKER = os.getenv("MQTT_BROKER", "localhost")
PORT = int(os.getenv("MQTT_PORT", "1883"))
TOPIC_IN = "dev/cdc"

MLAAS_URL = os.getenv("MLAAS_URL", "http://localhost:5000/predict")

NATS_URL = os.getenv("NATS_URL", "nats://localhost:4222")
NATS_TOPIC = "ml/predictions"

buffer = {}
nats_client = None
nats_loop = asyncio.new_event_loop()


async def init_nats():
    global nats_client
    nats_client = await nats.connect(NATS_URL)
    logger.info(f"Connected to NATS at {NATS_URL}")


def run_nats_loop():
    asyncio.set_event_loop(nats_loop)
    nats_loop.run_until_complete(init_nats())
    nats_loop.run_forever()


threading.Thread(target=run_nats_loop, daemon=True).start()


async def publish_prediction(msg):
    if nats_client and nats_client.is_connected:
        await nats_client.publish(NATS_TOPIC, json.dumps(msg).encode())


def schedule_publish(msg):
    if nats_client and nats_client.is_connected:
        nats_loop.call_soon_threadsafe(lambda: asyncio.create_task(publish_prediction(msg)))
    else:
        logger.warning("Tried to publish but NATS client not connected")


def call_mlaas(pump1, pump2, water_level, timestamp):
    payload = {
        "pump1_power_1": float(pump1[0]),
        "pump1_power_2": float(pump1[1]),
        "pump1_power_3": float(pump1[2]),
        "pump2_power_1": float(pump2[0]),
        "pump2_power_2": float(pump2[1]),
        "pump2_power_3": float(pump2[2]),
        "water_level": float(water_level) if water_level is not None else None,
    }
    try:
        response = requests.post(MLAAS_URL, json=payload, timeout=5)
        response.raise_for_status()
        result = response.json()
        logger.info(f"MLaaS response for {payload}: {result}")

        prediction_msg = {
            "actualWaterLevel": payload.get("water_level"),
            "predictedWaterLevel": result.get("prediction"),
            "timestamp": timestamp
        }

        schedule_publish(prediction_msg)
        logger.info(f"Scheduled publish to NATS {NATS_TOPIC}: {prediction_msg}")

    except Exception as e:
        logger.exception(f"Failed to call MLaaS: {e}")


def process_message(message):
    data = message.get("data", {})
    msg_type = message.get("type")

    timestamp = None
    pump_id = None
    pump_power = None
    water_level = None

    if msg_type == "WaterTankWaterLevelReading":
        timestamp = data.get("readingTime")
        water_level = data.get("waterLevel")

    elif msg_type == "PowerReading":
        timestamp = data.get("readingTime")
        pump_id = data.get("pump", "").strip().lower().replace(" ", "")
        pump_power = [
            data.get("channel1Power", 0),
            data.get("channel2Power", 0),
            data.get("channel3Power", 0),
        ]

    if not timestamp:
        logger.warning("Message without readingTime, skipping")
        return

    if timestamp not in buffer:
        buffer[timestamp] = {}

    entry = buffer[timestamp]

    if pump_id and pump_power:
        entry[pump_id] = pump_power
    elif water_level is not None:
        entry["water"] = water_level

    if "pump1" in entry and "pump2" in entry and "water" in entry:
        call_mlaas(entry["pump1"], entry["pump2"], entry["water"], timestamp)
        del buffer[timestamp]


def on_connect(client, userdata, flags, rc, properties=None):
    logger.info(f"Connected with result code {rc}")
    client.subscribe(TOPIC_IN)
    logger.info(f"Subscribed to topic: {TOPIC_IN}")


def on_message(client, userdata, msg):
    try:
        payload = msg.payload.decode()
        data = json.loads(payload)
        logger.info(f"Received message on {msg.topic}: {json.dumps(data)}")
        process_message(data)
    except Exception as e:
        logger.exception(f"Failed to process message: {e}")


if __name__ == "__main__":
    client = mqtt.Client(client_id="AnalyticsService")
    client.on_connect = on_connect
    client.on_message = on_message

    logger.info(f"Connecting to MQTT {BROKER}:{PORT} as AnalyticsService")
    client.connect(BROKER, PORT, 60)
    client.loop_forever()
