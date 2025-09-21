import React, { useEffect, useMemo, useRef, useState } from "react";
import mqtt from "mqtt";
import {
  Card,
  Text,
  Title,
  ScrollArea,
  Stack,
  Group,
  Badge,
  Box,
  rem,
} from "@mantine/core";

interface EventMessage {
  type: string;
  data: Record<string, any>;
}

type UIEvent = EventMessage & { id: string };

const WATER_LEVEL_MIN = 500;
const WATER_LEVEL_MAX = 850;
const POWER_MIN = 5.0;
const POWER_MAX = 11.5;
const MAX_EVENTS = 50;

const UnexpectedEventsViewer: React.FC = () => {
  const [powerEvents, setPowerEvents] = useState<UIEvent[]>([]);
  const [waterEvents, setWaterEvents] = useState<UIEvent[]>([]);
  const idCounter = useRef(0);

  const nextId = () => {
    idCounter.current += 1;
    return `${Date.now()}-${idCounter.current}`;
  };

  useEffect(() => {
    const client = mqtt.connect("ws://localhost:8083/mqtt");

    client.on("connect", () => {
      client.subscribe("dev/unexpected", (err) => {
        if (!err) console.log("Subscribed to dev/unexpected");
      });
    });

    client.on("message", (_, payload) => {
      try {
        const base = JSON.parse(payload.toString()) as EventMessage;
        const msg: UIEvent = { ...base, id: nextId() };

        if (msg.type === "PowerReading") {
          setPowerEvents((prev) => [msg, ...prev].slice(0, MAX_EVENTS));
        } else if (msg.type === "WaterTankWaterLevelReading") {
          setWaterEvents((prev) => [msg, ...prev].slice(0, MAX_EVENTS));
        }
      } catch (err) {}
    });

    return () => {
      client.end();
    };
  }, []);

  const describeEvent = (msg: EventMessage): string => {
    if (msg.type === "WaterTankWaterLevelReading") {
      const level = msg.data.waterLevel;
      if (level === 0) return "Sensor might be broken: water level is 0.";
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
      if (ch1 === 0 || ch2 === 0 || ch3 === 0)
        return "One or more power sensors might be broken (zero reading).";
      const bad: string[] = [];
      if (ch1 < POWER_MIN || ch1 > POWER_MAX) bad.push(`Ch1 ${ch1}`);
      if (ch2 < POWER_MIN || ch2 > POWER_MAX) bad.push(`Ch2 ${ch2}`);
      if (ch3 < POWER_MIN || ch3 > POWER_MAX) bad.push(`Ch3 ${ch3}`);
      if (bad.length)
        return `Unexpected power values → ${bad.join(
          ", "
        )} (range ${POWER_MIN}-${POWER_MAX}).`;
      return "Unexpected power reading event.";
    }
    return "Unknown event type.";
  };

  const EventCard = ({ e }: { e: UIEvent }) => {
    const ts = e.data.timestamp ?? e.data.time ?? e.data.ts;
    const summary = useMemo(() => describeEvent(e), [e]);

    return (
      <Card withBorder shadow="xs" radius="md" p="sm">
        <Group justify="space-between" align="center" mb={4}>
          <Group gap="xs">
            <Badge
              variant="light"
              color={e.type === "PowerReading" ? "red" : "teal"}
            >
              {e.type}
            </Badge>
            <Text size="sm" fw={600}>
              {summary}
            </Text>
          </Group>
          {ts && (
            <Text size="xs" c="dimmed" style={{ whiteSpace: "nowrap" }}>
              {String(ts)}
            </Text>
          )}
        </Group>

        <Box
          component="pre"
          style={{
            fontSize: 12,
            margin: 0,
            background: "rgba(0,0,0,0.02)",
            padding: rem(8),
            borderRadius: rem(8),
            overflowX: "auto",
          }}
        >
          {JSON.stringify(e.data, null, 2)}
        </Box>
      </Card>
    );
  };

  const Section = ({
    title,
    color,
    events,
  }: {
    title: string;
    color: string;
    events: UIEvent[];
  }) => (
    <Card
      withBorder
      shadow="xs"
      radius="md"
      style={{ flex: 1, display: "flex", flexDirection: "column" }}
    >
      <Title order={3} c={color} mb="xs">
        {title}
      </Title>
      <ScrollArea style={{ height: "48vh" }}>
        <Stack gap="sm">
          {events.map((e) => (
            <EventCard key={e.id} e={e} />
          ))}
        </Stack>
      </ScrollArea>
    </Card>
  );

  return (
    <Stack gap="lg">
      <Group align="stretch" gap="lg">
        <Section title="Power Events" color="blue" events={powerEvents} />
        <Section title="Water Level Events" color="teal" events={waterEvents} />
      </Group>
    </Stack>
  );
};

export default UnexpectedEventsViewer;
