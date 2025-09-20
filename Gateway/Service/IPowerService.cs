
namespace Gateway.Service;

public interface IPowerService
{
    Task RegisterReading(PowerReading reading);
    Task UpdateReading(PowerReading reading);
    Task DeleteReading(DeletePowerReadingDto dto);
    Task<PowerReading?> GetReadingAtTime(string waterTank, string pump, DateTime readingTime);
    Task<IEnumerable<PowerReading>> GetReadingsInInterval(string waterTank, string pump, DateTime startTime, DateTime endTime);
    Task<PowerAggregate> GetAggregatesForTimePeriod(string waterTank, string pump, DateTime startTime, DateTime endTime);
}