namespace Gateway.Controllers;

[Route("api/v1/[controller]")]
[ApiController]
public class WaterTankController : ControllerBase
{
    private readonly IWaterTankWaterLevelService _waterLevelService;
    private readonly ILogger<WaterTankController> _logger;

    public WaterTankController(IWaterTankWaterLevelService waterLevelService, ILogger<WaterTankController> logger)
    {
        _waterLevelService = waterLevelService;
        _logger = logger;
    }

    [HttpPost("")]
    public async Task<IActionResult> RegisterReading([FromBody] WaterTankWaterLevelReading waterLevelReading)
    {
        _logger.LogInformation("RegisterReading called at {Time} with {Readings}", DateTime.UtcNow, waterLevelReading);
        try
        {
            await _waterLevelService.RegisterReading(waterLevelReading);
            return Ok("Reading registered successfully.");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Couldn't register reading.");
            return BadRequest("Failed to register reading.");
        }
    }

    [HttpPut("")]
    public async Task<IActionResult> UpdateReading([FromBody] WaterTankWaterLevelReading waterLevelReading)
    {
        _logger.LogInformation("UpdateReading called at {Time} with {Readings}", DateTime.UtcNow, waterLevelReading);
        try
        {
            await _waterLevelService.UpdateReading(waterLevelReading);
            return Ok("Reading updated successfully.");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Couldn't update reading.");
            return BadRequest("Failed to update reading.");
        }
    }

    [HttpDelete("")]
    public async Task<IActionResult> DeleteReading([FromBody] DeleteWaterTankWaterLevelReadingDto deleteReadingDto)
    {
        _logger.LogInformation("UpdateReading called at {Time} with {ToDelete}", DateTime.UtcNow, deleteReadingDto);
        try
        {
            await _waterLevelService.DeleteReading(deleteReadingDto);
            return Ok("Reading deleted successfully.");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Couldn't delete reading.");
            return BadRequest("Failed to delete reading.");
        }
    }

    [HttpGet("")]
    public async Task<ActionResult<WaterTankWaterLevelReading>> GetWaterTankWaterLevelReadingAtTime(
        [FromQuery] string waterTank, [FromQuery] DateTime readingTime)
    {
        _logger.LogInformation("GetWaterLevelReading called at {Time} for {tank}", DateTime.UtcNow, waterTank);
        try
        {
            var result = await _waterLevelService.GetReadingAtTime(waterTank, readingTime);
            if (result == null) return NotFound();
            return Ok(result);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching reading.");
            return BadRequest("Failed to fetch reading.");
        }
    }

    [HttpGet("in-interval")]
    public async Task<ActionResult<List<WaterTankWaterLevelReading>>> GetWaterTankWaterLevelReadingsInInterval(
        [FromQuery] string waterTank, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime)
    {
        _logger.LogInformation("GetWaterLevelReadingInInterval called at {Time} for {tank} between {start} and {end}",
                                DateTime.UtcNow, waterTank, startTime, endTime);
        try
        {
            var readings = await _waterLevelService.GetReadingsInInterval(waterTank, startTime, endTime);
            return Ok(readings.ToList());
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching readings in interval.");
            return BadRequest("Failed to fetch readings in interval.");
        }
    }

    [HttpGet("agg")]
    public async Task<ActionResult<WaterTankWaterLevelAggregate>> GetWaterTankWaterLevelReadingAggregatesForTimePeriod(
        [FromQuery] string waterTank, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime)
    {
        _logger.LogInformation("GetWaterLevelAggregateReadingInInterval called at {Time} for {tank} between {start} and {end}",
                                DateTime.UtcNow, waterTank, startTime, endTime);
        try
        {
            var aggregate = await _waterLevelService.GetAggregatesForTimePeriod(waterTank, startTime, endTime);
            return Ok(aggregate);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching aggregate readings.");
            return BadRequest("Failed to fetch aggregate readings.");
        }
    }
}
