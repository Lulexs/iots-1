
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
    public IActionResult RegisterReading([FromBody] PowerReading powerReading)
    {
        _logger.LogInformation("RegisterReading called at {Time} with {Readings}", DateTime.Now, powerReading);

        return Ok();
    }

    [HttpPut("")]
    public IActionResult UpdateReading([FromBody] PowerReading powerReading)
    {
        return Ok();
    }

    [HttpDelete("")]
    public IActionResult DeleteReading([FromBody] DeleteWaterTankWaterLevelReadingDto deleteReadingDto)
    {
        return Ok();
    }

    [HttpGet("")]
    public ActionResult<PowerReading> GetPowerReadingAtTime(
        [FromQuery] string waterTank, [FromQuery] string pump, [FromQuery] DateTime readingTime)
    {
        return Ok();
    }

    [HttpGet("/in-interval")]
    public ActionResult<List<PowerReading>> GetWaterTankWaterLevelReadingsInInterval(
        [FromQuery] string waterTank, [FromQuery] string pump, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime
    )
    {
        return Ok();
    }

    [HttpGet("/agg")]
    public ActionResult<PowerAggregate> GetWaterTankWaterLevelReadingAggregatesForTimePerido(
        [FromQuery] string waterTank, [FromQuery] DateTime startTime, [FromQuery] DateTime endTime
    )
    {
        return Ok();
    }
}
