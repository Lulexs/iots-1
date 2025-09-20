namespace Gateway.Service;

public interface IWaterTankWaterLevelService
{
    Task RegisterReading(WaterTankWaterLevelReading reading);
    Task UpdateReading(WaterTankWaterLevelReading reading);
    Task DeleteReading(DeleteWaterTankWaterLevelReadingDto dto);
    Task<WaterTankWaterLevelReading?> GetReadingAtTime(string waterTank, DateTime readingTime);
    Task<IEnumerable<WaterTankWaterLevelReading>> GetReadingsInInterval(
        string waterTank,
        DateTime startTime,
        DateTime endTime);
    Task<WaterTankWaterLevelAggregate> GetAggregatesForTimePeriod(
        string waterTank,
        DateTime startTime,
        DateTime endTime);
}
