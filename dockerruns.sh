docker run -d \
  --name datamanager-postgres \
  --network microservice-net \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  postgres:latest

docker run -d \
  --name datamanager-service \
  --network microservice-net \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://datamanager-postgres:5432/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -p 9090:9090 \
  datamanager-service:1.0

docker run -d \
  --name gateway-service \
  --network microservice-net \
  -e SPRINGBOOT_SERVICE_URL=http://datamanager-service:9090 \
  -e ASPNETCORE_URLS=http://+:5103 \
  -p 5103:5103 \
  gateway-service:1.0
