namespace Gateway.Model;

public record PowerReading(string WaterTank,
                           string Pump,
                           DateTime ReadingTime,
                           double Channel1Power,
                           double Channel2Power,
                           double Channel3Power);