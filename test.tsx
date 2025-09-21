import React, { useEffect, useState } from "react";
import mqtt from "mqtt";
import { motion } from "framer-motion";

interface EventMessage {
  type: string;
  data: Record<string, any>;
}

const WATER_LEVEL_MIN = 500;
const WATER_LEVEL_MAX = 850;
const POWER_MIN = 5.0;
const POWER_MAX = 11.5;

const UnexpectedEventsViewer: React.FC = () => {
  const [powerEvents, setPowerEvents] = useState<EventMessage[]>([]);
  const [waterEvents, setWaterEvents] = useState<EventMessage[]>([]);

  useEffect(() => {
    const client = mqtt.connect("ws://localhost:8083/mqtt");

    client.on("connect", () => {
      console.log("Connected to MQTT broker via WebSocket");
      client.subscribe("dev/unexpected", (err) => {
        if (!err) {
          console.log("Subscribed to dev/unexpected");
        }
      });
    });

    client.on("message", (_, payload) => {
      try {
        const msg = JSON.parse(payload.toString()) as EventMessage;
        if (msg.type === "PowerReading") {
          setPowerEvents((prev) => [msg, ...prev]);
        } else if (msg.type === "WaterTankWaterLevelReading") {
          setWaterEvents((prev) => [msg, ...prev]);
        }
      } catch (err) {
        console.error("Failed to parse MQTT message", err);
      }
    });

    return () => {
      client.end();
    };
  }, []);

  const describeEvent = (msg: EventMessage): string => {
    if (msg.type === "WaterTankWaterLevelReading") {
      const level = msg.data.waterLevel;
      if (level === 0) return `Sensor might be broken: water level is 0.`;
      if (level < WATER_LEVEL_MIN)
        return `Water level too low: ${level} (min ${WATER_LEVEL_MIN}).`;
      if (level > WATER_LEVEL_MAX)
        return `Water level too high: ${level} (max ${WATER_LEVEL_MAX}).`;
      return `Unexpected water level event at ${level}.`;
    }

    if (msg.type === "PowerReading") {
      const ch1 = msg.data.channel1Power ?? 0;
      const ch2 = msg.data.channel2Power ?? 0;
      const ch3 = msg.data.channel3Power ?? 0;

      if (ch1 === 0 || ch2 === 0 || ch3 === 0) {
        return `One or more power sensors might be broken (zero reading detected).`;
      }

      const exceeded: string[] = [];
      if (ch1 < POWER_MIN || ch1 > POWER_MAX)
        exceeded.push(`Channel 1: ${ch1}`);
      if (ch2 < POWER_MIN || ch2 > POWER_MAX)
        exceeded.push(`Channel 2: ${ch2}`);
      if (ch3 < POWER_MIN || ch3 > POWER_MAX)
        exceeded.push(`Channel 3: ${ch3}`);

      if (exceeded.length > 0) {
        return `Unexpected power values detected → ${exceeded.join(
          ", "
        )} (range ${POWER_MIN}-${POWER_MAX}).`;
      }
      return `Unexpected power reading event.`;
    }

    return "Unknown event type.";
  };

  const EventCard: React.FC<{ msg: EventMessage }> = ({ msg }) => {
    const [open, setOpen] = useState(false);

    return (
      <motion.div
        layout
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white shadow-lg rounded-xl p-4 border border-red-200 hover:shadow-xl transition-all duration-200 hover:border-red-300"
      >
        <div
          className="cursor-pointer"
          onClick={() => setOpen((prev) => !prev)}
        >
          <div className="flex items-center justify-between mb-2">
            <h2 className="font-bold text-red-600 text-lg">{msg.type}</h2>
            <div className="w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
          </div>
          <p className="text-gray-700 leading-relaxed">{describeEvent(msg)}</p>
          <p className="text-xs text-gray-400 mt-3 flex items-center">
            <span className="mr-1">👁️</span>
            Click to {open ? "hide" : "show"} raw data
          </p>
        </div>
        {open && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            className="overflow-hidden"
          >
            <pre className="text-sm bg-gray-50 rounded-lg p-3 mt-3 overflow-x-auto border border-gray-200 font-mono">
              {JSON.stringify(msg.data, null, 2)}
            </pre>
          </motion.div>
        )}
      </motion.div>
    );
  };

  const DashboardPanel: React.FC<{
    title: string;
    events: EventMessage[];
    emptyMessage: string;
    titleColor: string;
    gradientFrom: string;
    gradientTo: string;
  }> = ({
    title,
    events,
    emptyMessage,
    titleColor,
    gradientFrom,
    gradientTo,
  }) => (
    <div className="flex flex-col h-full">
      <div
        className={`bg-gradient-to-r ${gradientFrom} ${gradientTo} rounded-t-2xl p-6 shadow-lg`}
      >
        <h1 className={`text-2xl font-bold ${titleColor} flex items-center`}>
          <span className="mr-3 text-3xl">
            {title.includes("Power") ? "⚡" : "💧"}
          </span>
          {title}
        </h1>
        <p className={`${titleColor} opacity-80 mt-1`}>
          {events.length} unexpected events detected
        </p>
      </div>

      <div className="flex-1 bg-gray-50 rounded-b-2xl p-6 overflow-hidden flex flex-col">
        <div className="flex-1 overflow-y-auto space-y-4 pr-2 scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100">
          {events.map((msg, idx) => (
            <EventCard key={idx} msg={msg} />
          ))}
          {events.length === 0 && (
            <div className="flex items-center justify-center h-full">
              <div className="text-center text-gray-500">
                <div className="text-6xl mb-4">✅</div>
                <p className="text-lg font-medium">{emptyMessage}</p>
                <p className="text-sm mt-2">All systems operating normally</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );

  return (
    <div className="h-screen bg-gradient-to-br from-slate-100 via-blue-50 to-purple-50 p-6 overflow-hidden">
      <div className="max-w-7xl mx-auto h-full">
        <div className="mb-6">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
            System Monitoring Dashboard
          </h1>
          <p className="text-gray-600 mt-2">
            Real-time unexpected events monitoring
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 h-[calc(100%-120px)]">
          <DashboardPanel
            title="Power Events"
            events={powerEvents}
            emptyMessage="No unexpected power events"
            titleColor="text-white"
            gradientFrom="from-blue-600"
            gradientTo="to-blue-800"
          />

          <DashboardPanel
            title="Water Level Events"
            events={waterEvents}
            emptyMessage="No unexpected water events"
            titleColor="text-white"
            gradientFrom="from-emerald-600"
            gradientTo="to-emerald-800"
          />
        </div>
      </div>
    </div>
  );
};

export default UnexpectedEventsViewer;
