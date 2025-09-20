
namespace Gateway.Controllers;

[ApiController]
[Route("health")]
public class ReadinessController : ControllerBase
{
    [HttpGet("ready")]
    public IActionResult Ready()
    {
        bool isReady = CheckServiceReadiness();

        if (isReady)
        {
            return Ok(new { status = "ready" });
        }
        else
        {
            return StatusCode(503, new { status = "not ready" });
        }
    }

    private bool CheckServiceReadiness()
    {
        return true;
    }
}
