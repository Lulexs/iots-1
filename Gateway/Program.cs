using Serilog;
using Serilog.Filters;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddScoped<IWaterTankWaterLevelService, WaterTankWaterLevelService>();
builder.Services.AddScoped<IPowerService, PowerService>();

builder.Logging.ClearProviders();
Log.Logger = new LoggerConfiguration()
    .MinimumLevel.Information()
    .WriteTo.Logger(lc => lc
        .Filter.ByIncludingOnly(Matching.FromSource("Gateway.Controllers.WaterTankController"))
        .WriteTo.File("Logs/WaterTankController.log", rollingInterval: RollingInterval.Day))
    .WriteTo.Logger(lc => lc
        .Filter.ByIncludingOnly(Matching.FromSource("Gateway.Controllers.PowerController"))
        .WriteTo.File("Logs/PowerController.log", rollingInterval: RollingInterval.Day))
    .CreateLogger();

builder.Host.UseSerilog();

// Add services to the container.
builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.UseSwaggerUI(options =>
    {
        options.SwaggerEndpoint("/openapi/v1.json", "v1");
    });
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
