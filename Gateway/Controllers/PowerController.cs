
namespace Gateway.Controllers;

[Route("api/v1/[controller]")]
[ApiController]
public class PowerController : ControllerBase
{
    private readonly IPowerService _powerService;
    private readonly ILogger<PowerController> _logger;

    public PowerController(IPowerService powerService, ILogger<PowerController> logger)
    {
        _powerService = powerService;
        _logger = logger;
    }

    [HttpPost("")]
    public async Task<IActionResult> RegisterReading([FromBody] PowerReading powerReading)
    {
        _logger.LogInformation("RegisterReading called at {Time} with {Readings}", DateTime.Now, powerReading);

        try
        {
            await _powerService.RegisterReading(powerReading);
            return Ok();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error registering power reading");
            return BadRequest();
        }
    }

    [HttpPut("")]
    public async Task<IActionResult> UpdateReading([FromBody] PowerReading powerReading)
    {
        _logger.LogInformation("UpdateReading called at {Time} with {Readings}", DateTime.Now, powerReading);

        try
        {
            await _powerService.UpdateReading(powerReading);
            return Ok();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating power reading");
            return BadRequest();
        }
    }

    [HttpDelete("")]
    public async Task<IActionResult> DeleteReading([FromBody] DeletePowerReadingDto deleteReadingDto)
    {
        _logger.LogInformation("DeleteReading called at {Time} with {DeleteDto}", DateTime.Now, deleteReadingDto);

        try
        {
            await _powerService.DeleteReading(deleteReadingDto);
            return Ok();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting power reading");
            return BadRequest();
        }
    }

    [HttpGet("")]
    public async Task<ActionResult<PowerReading>> GetPowerReadingAtTime(
        [FromQuery] string waterTank, [FromQuery] string pump, [FromQuery] DateTime readingTime)
    {
        _logger.LogInformation("GetPowerReading called at {Time} for {tank} and {pump} at {time}",
                    DateTime.UtcNow, waterTank, pump, readingTime);
        try
        {
            var reading = await _powerService.GetReadingAtTime(waterTank, pump, readingTime);
            if (reading == null) return NotFound();
            return Ok(reading);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching power reading at time");
            return BadRequest();
        }
    }

    [HttpGet("in-interval")]
    public async Task<ActionResult<List<PowerReading>>> GetReadingsInInterval(
        [FromQuery] string waterTank, [FromQuery] string pump,
        [FromQuery] DateTime startTime, [FromQuery] DateTime endTime)
    {
        _logger.LogInformation("GetPowerInInterval called at {Time} for {tank} and {pump} between {start} and {end}",
                        DateTime.UtcNow, waterTank, pump, startTime, endTime);
        try
        {
            var readings = await _powerService.GetReadingsInInterval(waterTank, pump, startTime, endTime);
            return Ok(readings);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching power readings in interval");
            return BadRequest();
        }
    }

    [HttpGet("agg")]
    public async Task<ActionResult<PowerAggregate>> GetAggregatesForTimePeriod(
        [FromQuery] string waterTank, [FromQuery] string pump,
        [FromQuery] DateTime startTime, [FromQuery] DateTime endTime)
    {
        _logger.LogInformation("GetAggregatesInInterval called at {Time} for {tank} and {pump} between {start} and {end}",
                                DateTime.UtcNow, waterTank, pump, startTime, endTime);
        try
        {
            var aggregate = await _powerService.GetAggregatesForTimePeriod(waterTank, pump, startTime, endTime);
            return Ok(aggregate);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error fetching power aggregates");
            return BadRequest();
        }
    }
}
