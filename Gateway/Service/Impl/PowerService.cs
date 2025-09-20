using Google.Protobuf.WellKnownTypes;

namespace Gateway.Service.Impl;

public class PowerService : IPowerService
{
    private readonly Protos.PowerService.PowerServiceClient _client;

    public PowerService(Protos.PowerService.PowerServiceClient client)
    {
        _client = client;
    }

    public async Task RegisterReading(PowerReading reading)
    {
        await _client.RegisterReadingAsync(new Protos.PowerReading
        {
            WaterTank = reading.WaterTank,
            Pump = reading.Pump,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(reading.ReadingTime, DateTimeKind.Utc)),
            Channel1Power = reading.Channel1Power,
            Channel2Power = reading.Channel2Power,
            Channel3Power = reading.Channel3Power
        });
    }

    public async Task UpdateReading(PowerReading reading)
    {
        await _client.UpdateReadingAsync(new Protos.PowerReading
        {
            WaterTank = reading.WaterTank,
            Pump = reading.Pump,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(reading.ReadingTime, DateTimeKind.Utc)),
            Channel1Power = reading.Channel1Power,
            Channel2Power = reading.Channel2Power,
            Channel3Power = reading.Channel3Power
        });
    }

    public async Task DeleteReading(DeletePowerReadingDto dto)
    {
        await _client.DeleteReadingAsync(new Protos.DeletePowerReadingDto
        {
            WaterTank = dto.WaterTank,
            Pump = dto.Pump,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(dto.ReadingTime, DateTimeKind.Utc))
        });
    }

    public async Task<PowerReading?> GetReadingAtTime(string waterTank, string pump, DateTime readingTime)
    {
        var request = new Protos.GetPowerReadingRequest
        {
            WaterTank = waterTank,
            Pump = pump,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(readingTime, DateTimeKind.Utc))
        };

        var response = await _client.GetPowerReadingAtTimeAsync(request);

        if (response == null) return null;

        return new PowerReading(
            response.WaterTank,
            response.Pump,
            response.ReadingTime.ToDateTime(),
            response.Channel1Power,
            response.Channel2Power,
            response.Channel3Power
        );
    }

    public async Task<IEnumerable<PowerReading>> GetReadingsInInterval(string waterTank, string pump, DateTime startTime, DateTime endTime)
    {
        var request = new Protos.GetPowerReadingsIntervalRequest
        {
            WaterTank = waterTank,
            Pump = pump,
            StartTime = Timestamp.FromDateTime(DateTime.SpecifyKind(startTime, DateTimeKind.Utc)),
            EndTime = Timestamp.FromDateTime(DateTime.SpecifyKind(endTime, DateTimeKind.Utc))
        };

        var response = await _client.GetPowerReadingsInIntervalAsync(request);

        return response.Readings.Select(r => new PowerReading(
            r.WaterTank,
            r.Pump,
            r.ReadingTime.ToDateTime(),
            r.Channel1Power,
            r.Channel2Power,
            r.Channel3Power
        ));
    }

    public async Task<PowerAggregate> GetAggregatesForTimePeriod(string waterTank, string pump, DateTime startTime, DateTime endTime)
    {
        var request = new Protos.GetPowerReadingsIntervalRequest
        {
            WaterTank = waterTank,
            Pump = pump,
            StartTime = Timestamp.FromDateTime(DateTime.SpecifyKind(startTime, DateTimeKind.Utc)),
            EndTime = Timestamp.FromDateTime(DateTime.SpecifyKind(endTime, DateTimeKind.Utc))
        };

        var response = await _client.GetPowerReadingAggregatesForTimePeriodAsync(request);

        return new PowerAggregate(response.Min, response.Max, response.Avg, response.Sum);
    }
}
