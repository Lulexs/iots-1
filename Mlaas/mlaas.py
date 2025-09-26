from flask import Flask, request, jsonify
import joblib
import pandas as pd

app = Flask(__name__)

MODEL_PATH = "model.pkl"
model = joblib.load(MODEL_PATH)

FEATURES = [
    "pump1_power_1",
    "pump1_power_2",
    "pump1_power_3",
    "pump2_power_1",
    "pump2_power_2",
    "pump2_power_3",
]

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True)
    missing = [f for f in FEATURES if f not in data]
    print(data)
    if missing:
        return jsonify({"error": f"Missing features: {missing}"}), 400

    X = pd.DataFrame([[float(data[f]) for f in FEATURES]], columns=FEATURES)

    pred = model.predict(X)[0]
    return jsonify({"prediction": float(pred)})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)