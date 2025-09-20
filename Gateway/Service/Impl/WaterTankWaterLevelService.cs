using Gateway.Protos;
using Google.Protobuf.WellKnownTypes;

namespace Gateway.Service.Impl;

public class WaterTankWaterLevelService : IWaterTankWaterLevelService
{
    private readonly WaterTankService.WaterTankServiceClient _client;

    public WaterTankWaterLevelService(WaterTankService.WaterTankServiceClient client)
    {
        _client = client;
    }

    public async Task RegisterReading(Model.WaterTankWaterLevelReading reading)
    {
        await _client.RegisterReadingAsync(new Protos.WaterTankWaterLevelReading
        {
            WaterTankName = reading.WaterTankName,
            WaterLevel = reading.WaterLevel,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(reading.ReadingTime, DateTimeKind.Utc)),
        });
    }

    public async Task UpdateReading(Model.WaterTankWaterLevelReading reading)
    {
        await _client.UpdateReadingAsync(new Protos.WaterTankWaterLevelReading
        {
            WaterTankName = reading.WaterTankName,
            WaterLevel = reading.WaterLevel,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(reading.ReadingTime, DateTimeKind.Utc)),
        });
    }

    public async Task DeleteReading(Model.DeleteWaterTankWaterLevelReadingDto dto)
    {
        await _client.DeleteReadingAsync(new Protos.DeleteWaterTankWaterLevelReadingDto
        {
            WaterTank = dto.WaterTank,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(dto.ReadingTime, DateTimeKind.Utc)),
        });
    }

    public async Task<Model.WaterTankWaterLevelReading?> GetReadingAtTime(string waterTank, DateTime readingTime)
    {
        var response = await _client.GetWaterTankWaterLevelReadingAtTimeAsync(new GetReadingRequest
        {
            WaterTank = waterTank,
            ReadingTime = Timestamp.FromDateTime(DateTime.SpecifyKind(readingTime, DateTimeKind.Utc)),
        });

        if (response == null) return null;

        return new Model.WaterTankWaterLevelReading(
            response.WaterTankName,
            response.ReadingTime.ToDateTime(),
            response.WaterLevel
        );
    }

    public async Task<IEnumerable<Model.WaterTankWaterLevelReading>> GetReadingsInInterval(
        string waterTank,
        DateTime startTime,
        DateTime endTime)
    {
        var response = await _client.GetWaterTankWaterLevelReadingsInIntervalAsync(new GetReadingsIntervalRequest
        {
            WaterTank = waterTank,
            StartTime = Timestamp.FromDateTime(DateTime.SpecifyKind(startTime, DateTimeKind.Utc)),
            EndTime = Timestamp.FromDateTime(DateTime.SpecifyKind(endTime, DateTimeKind.Utc)),
        });

        return response.Readings.Select(r => new Model.WaterTankWaterLevelReading(
            r.WaterTankName,
            r.ReadingTime.ToDateTime(),
            r.WaterLevel
        ));
    }

    public async Task<Model.WaterTankWaterLevelAggregate> GetAggregatesForTimePeriod(
        string waterTank,
        DateTime startTime,
        DateTime endTime)
    {
        var response = await _client.GetWaterTankWaterLevelReadingAggregatesForTimePeriodAsync(
            new GetReadingsIntervalRequest
            {
                WaterTank = waterTank,
                StartTime = Timestamp.FromDateTime(DateTime.SpecifyKind(startTime, DateTimeKind.Utc)),
                EndTime = Timestamp.FromDateTime(DateTime.SpecifyKind(endTime, DateTimeKind.Utc)),
            });

        return new Model.WaterTankWaterLevelAggregate(
            response.Min,
            response.Max,
            response.Avg,
            response.Sum
        );
    }
}
