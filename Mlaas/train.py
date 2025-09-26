import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
import joblib

pump1 = pd.read_csv("pump1_power_50k.csv")
pump2 = pd.read_csv("pump2_power_50k.csv")
water = pd.read_csv("water_level_50k.csv")

pump1.columns = ["timestamp", "pump1_power_1", "pump1_power_2", "pump1_power_3"]
pump2.columns = ["timestamp", "pump2_power_1", "pump2_power_2", "pump2_power_3"]
water.columns = ["timestamp", "water_level"]

df = water.merge(pump1, on="timestamp", how="inner")
df = df.merge(pump2, on="timestamp", how="inner")
X = df.drop(columns=["timestamp", "water_level"])
y = df["water_level"]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=1515)

model = RandomForestRegressor(n_estimators=100, random_state=1515)
model.fit(X_train, y_train)

preds = model.predict(X_test)
mse = mean_squared_error(y_test, preds)
print(f"Regression model trained. MSE: {mse:.4f}")

joblib.dump(model, "model.pkl")
print("Model saved to model.pkl")