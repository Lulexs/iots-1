import pandas as pd

pump1_file = "Power Sensor - NeWater Pump 1.csv"
pump2_file = "Power Sensor - NeWater Pump 2.csv"
water_file = "Water Level Sensor - NeWater Tank.csv"

pump1_out = "pump1_power_50k.csv"
pump2_out = "pump2_power_50k.csv"
water_out = "water_level_50k.csv"

N = 50000

def trim_csv(input_file, output_file, n_rows):
    df = pd.read_csv(input_file, nrows=n_rows)
    df.to_csv(output_file, index=False)
    print(f"Saved {len(df)} rows from {input_file} -> {output_file}")

trim_csv(pump1_file, pump1_out, N)
trim_csv(pump2_file, pump2_out, N)
trim_csv(water_file, water_out, N)
