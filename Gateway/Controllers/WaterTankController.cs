
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
    public IActionResult RegisterReading([FromBody] WaterTankWaterLevelReading waterLevelReading)
    {
        _logger.LogInformation("RegisterReading called at {Time} with {Readings}", DateTime.Now, waterLevelReading);

        return Ok();
    }

    [HttpPut("")]
    public IActionResult UpdateReading([FromBody] WaterTankWaterLevelReading waterTankWaterLevelReading)
    {
        return Ok();
    }

    [HttpDelete("")]
    public IActionResult DeleteReading([FromBody] DeleteWaterTankWaterLevelReadingDto deleteReadingDto)
    {
        return Ok();
    }

    [HttpGet("")]
    public ActionResult<WaterTankWaterLevelReading> GetWaterTankWaterLevelReadingAtTime(
        [FromQuery] string waterTank, [FromQuery] DateTime readingTime)
    {
        return Ok();
    }

    [HttpGet("/in-interval")]
    public ActionResult<List<WaterTankWaterLevelReading>> GetWaterTankWaterLevelReadingsInInterval(
        [FromQuery] string waterTank, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime
    )
    {
        return Ok();
    }

    [HttpGet("/agg")]
    public ActionResult<WaterTankWaterLevelAggregate> GetWaterTankWaterLevelReadingAggregatesForTimePerido(
        [FromQuery] string waterTank, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime
    )
    {
        return Ok();
    }

}