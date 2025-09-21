import os
import json
import logging
from logging.handlers import RotatingFileHandler
import paho.mqtt.client as mqtt

LOG_FILE = "eventmanager.log"
handler = RotatingFileHandler(LOG_FILE, maxBytes=5 * 1024 * 1024, backupCount=3)
formatter = logging.Formatter(
    fmt="%(asctime)s %(levelname)s %(name)s %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
handler.setFormatter(formatter)

logger = logging.getLogger("eventmanager")
logger.setLevel("INFO")
logger.addHandler(handler)

BROKER = os.getenv("MQTT_BROKER", "localhost")
PORT = int(os.getenv("MQTT_PORT", "1883"))
TOPIC_IN = "dev/cdc"
TOPIC_OUT = "dev/unexpected"

WATER_LEVEL_MIN = 500
WATER_LEVEL_MAX = 800

POWER_MIN = 5.0
POWER_MAX = 11.5

def process_message(client, message):
    msg_type = message.get("type")
    data = message.get("data", {})

    if msg_type == "WaterTankWaterLevelReading":
        water_level = data.get("waterLevel")
        if water_level is not None and (water_level < WATER_LEVEL_MIN or water_level > WATER_LEVEL_MAX):
            logger.warning(f"Unexpected water level detected: {water_level}")
            client.publish(TOPIC_OUT, json.dumps(message))
            logger.info(f"Published to {TOPIC_OUT}")

    elif msg_type == "PowerReading":
        ch1 = data.get("channel1Power")
        ch2 = data.get("channel2Power")
        ch3 = data.get("channel3Power")

        if any(ch is not None and (ch < POWER_MIN or ch > POWER_MAX) for ch in [ch1, ch2, ch3]):
            logger.warning(f"Unexpected power reading detected: ch1={ch1}, ch2={ch2}, ch3={ch3}")
            client.publish(TOPIC_OUT, json.dumps(message))
            logger.info(f"Published to {TOPIC_OUT}")

def on_connect(client, userdata, flags, rc, properties=None):
    logger.info(f"Connected with result code {rc}")
    client.subscribe(TOPIC_IN)
    logger.info(f"Subscribed to topic: {TOPIC_IN}")

def on_message(client, userdata, msg):
    try:
        payload = msg.payload.decode()
        data = json.loads(payload)
        logger.debug(f"Received message on {msg.topic}: {json.dumps(data, indent=2)}")
        process_message(client, data)
    except Exception as e:
        logger.exception(f"Failed to process message: {e}")

if __name__ == "__main__":
    client = mqtt.Client(client_id="EventManager")
    client.on_connect = on_connect
    client.on_message = on_message

    logger.info(f"Connecting to MQTT {BROKER}:{PORT} as EventManager")
    client.connect(BROKER, PORT, 60)
    client.loop_forever()
