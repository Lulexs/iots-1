using Gateway.Model;

namespace Gateway.Service;

public interface IWaterTankWaterLevelService
{
    void RegisterReading(WaterTankWaterLevelReading reading);
}