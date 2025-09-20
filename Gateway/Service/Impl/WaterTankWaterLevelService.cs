
namespace Gateway.Service.Impl;

public class WaterTankWaterLevelService : IWaterTankWaterLevelService
{
    public WaterTankWaterLevelService()
    {

    }

    public void RegisterReading(WaterTankWaterLevelReading reading)
    {
        Console.WriteLine(reading);
        return;
    }
}