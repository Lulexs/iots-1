import csv
import time
import requests
from itertools import zip_longest

CSV_FILES = [
    "Water Level Sensor - NeWater Tank.csv",
    "Energy Sensor - NeWater Pump 1.csv",
    "Energy Sensor - NeWater Pump 2.csv",
    "Power Sensor - NeWater Pump 1.csv",
    "Power Sensor - NeWater Pump 2.csv",
]

WATER_LEVEL_ENDPOINT = "http://localhost:5103/api/v1/WaterTank"
# ENERGY_ENDPOINT = "http://localhost:5103/api/v1/PumpEnergy"
POWER_ENDPOINT = "http://localhost:5103/api/v1/Power"


def stream_csv(filename):
    with open(filename, newline='', encoding="utf-8") as f:
        reader = csv.reader(f)
        for row in reader:
            yield row


def main():
    streams = [stream_csv(f) for f in CSV_FILES]

    for rows in zip_longest(*streams, fillvalue=None):
        # Water Level
        water_row = rows[0]
        if water_row is not None:
            try:
                reading_time = water_row[0].strip().replace(" ", "T")
                water_level = float(water_row[1])

                payload = {
                    "waterTankName": "NeWater Tank",
                    "readingTime": reading_time,
                    "waterLevel": water_level,
                }

                response = requests.post(WATER_LEVEL_ENDPOINT, json=payload, timeout=10)
                print(f"Sent water row -> Status: {response.status_code}")
            except Exception as e:
                print(f"Failed to process water row: {e}")

        # Power - Pump 1
        power_row1 = rows[3]
        if power_row1 is not None:
            try:
                reading_time = power_row1[0].strip().replace(" ", "T")
                channel1 = float(power_row1[1].replace(" W", ""))
                channel2 = float(power_row1[2].replace(" W", ""))
                channel3 = float(power_row1[3].replace(" W", ""))

                payload = {
                    "WaterTank": "NeWater Tank",
                    "Pump": "Pump 1",
                    "ReadingTime": reading_time,
                    "Channel1Power": channel1,
                    "Channel2Power": channel2,
                    "Channel3Power": channel3,
                }

                response = requests.post(POWER_ENDPOINT, json=payload, timeout=10)
                print(f"Sent power row -> Status: {response.status_code}")
            except Exception as e:
                print(f"Failed to process power row: {e}")

        # Power - Pump 2
        power_row2 = rows[4]
        if power_row2 is not None:
            try:
                reading_time = power_row2[0].strip().replace(" ", "T")
                channel1 = float(power_row2[1].replace(" W", ""))
                channel2 = float(power_row2[2].replace(" W", ""))
                channel3 = float(power_row2[3].replace(" W", ""))

                payload = {
                    "WaterTank": "NeWater Tank",
                    "Pump": "Pump 2",
                    "ReadingTime": reading_time,
                    "Channel1Power": channel1,
                    "Channel2Power": channel2,
                    "Channel3Power": channel3,
                }

                response = requests.post(POWER_ENDPOINT, json=payload, timeout=10)
                print(f"Sent power row -> Status: {response.status_code}")
            except Exception as e:
                print(f"Failed to process power row: {e}")

        time.sleep(1)


if __name__ == "__main__":
    main()