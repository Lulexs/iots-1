import React, { useEffect, useRef, useState } from "react";
import { connect, StringCodec } from "nats.ws";
import { Card, Title, ScrollArea, Stack, Text } from "@mantine/core";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

interface PredictionMessage {
  predictedWaterLevel: number;
  actualWaterLevel: number;
  timestamp: number;
}

const MAX_POINTS = 50;

const PredictionEventsViewer: React.FC = () => {
  const [data, setData] = useState<PredictionMessage[]>([]);
  const idCounter = useRef(0);

  useEffect(() => {
    let isMounted = true;

    const run = async () => {
      try {
        const nc = await connect({ servers: "ws://localhost:11080" });
        const sc = StringCodec();

        const sub = nc.subscribe("ml/predictions");

        (async () => {
          for await (const m of sub) {
            if (!isMounted) break;
            try {
              const payload = JSON.parse(
                sc.decode(m.data)
              ) as PredictionMessage;
              idCounter.current += 1;
              const entry = {
                ...payload,
                timestamp: new Date(payload.timestamp).getTime(),
              };
              setData((prev) => [entry, ...prev].slice(0, MAX_POINTS));
            } catch (err) {
              console.error("Bad message", err);
            }
          }
        })();
      } catch (err) {
        console.error("NATS connection failed", err);
      }
    };

    run();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <Card withBorder shadow="xs" radius="md" p="sm" style={{ flex: 1 }}>
      <Title order={3} mb="sm" c="indigo">
        Water Level Predictions
      </Title>
      <ScrollArea style={{ height: "55vh" }}>
        <Stack gap="sm">
          {data.length === 0 ? (
            <Text size="sm" c="dimmed">
              Waiting for prediction events...
            </Text>
          ) : (
            <ResponsiveContainer width="100%" height={300}>
              <LineChart
                data={[...data].reverse()}
                margin={{ top: 10, right: 20, left: 0, bottom: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                  type="number"
                  dataKey="timestamp"
                  domain={["auto", "auto"]}
                  scale="time"
                  tickFormatter={(ts) =>
                    new Date(ts).toLocaleTimeString("en-US", {
                      hour12: false,
                      hour: "2-digit",
                      minute: "2-digit",
                      second: "2-digit",
                    })
                  }
                />
                <YAxis />
                <Tooltip
                  labelFormatter={(ts) =>
                    new Date(ts).toLocaleString("en-US", {
                      hour12: false,
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                      second: "2-digit",
                    })
                  }
                />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="predictedWaterLevel"
                  stroke="#8884d8"
                  dot={false}
                  name="Predicted"
                />
                <Line
                  type="monotone"
                  dataKey="actualWaterLevel"
                  stroke="#82ca9d"
                  dot={false}
                  name="Actual"
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </Stack>
      </ScrollArea>
    </Card>
  );
};

export default PredictionEventsViewer;
