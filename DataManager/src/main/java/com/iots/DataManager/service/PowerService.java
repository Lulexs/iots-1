package com.iots.DataManager.service;

import com.iots.DataManager.repository.PowerRepository;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@GrpcService
public class PowerService extends com.iots.grpc.power.PowerServiceGrpc.PowerServiceImplBase {

    private final PowerRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerService.class);

    public PowerService(PowerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void registerReading(com.iots.grpc.power.PowerReading request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        LOGGER.info("registerReading received: {}", request);
        repository.insertPowerReading(request);
        responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void updateReading(com.iots.grpc.power.PowerReading request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        LOGGER.info("updateReading received: {}", request);
        repository.updatePowerReading(request);
        responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void deleteReading(com.iots.grpc.power.DeletePowerReadingDto request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
        LOGGER.info("deleteReading received: {}", request);
        repository.deletePowerReading(request);
        responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getPowerReadingAtTime(com.iots.grpc.power.GetPowerReadingRequest request, StreamObserver<com.iots.grpc.power.PowerReading> responseObserver) {
        LOGGER.info("GetPowerReading received: {}", request);
        com.iots.grpc.power.PowerReading reading = repository.getPowerReadingAtTime(request);
        if (reading != null) {
            responseObserver.onNext(reading);
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getPowerReadingsInInterval(com.iots.grpc.power.GetPowerReadingsIntervalRequest request, StreamObserver<com.iots.grpc.power.PowerReadingsResponse> responseObserver) {
        LOGGER.info("getPowerInInterval received: {}", request);
        List<com.iots.grpc.power.PowerReading> readings = repository.getPowerReadingsInInterval(request);

        com.iots.grpc.power.PowerReadingsResponse response = com.iots.grpc.power.PowerReadingsResponse.newBuilder()
                .addAllReadings(readings)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getPowerReadingAggregatesForTimePeriod(com.iots.grpc.power.GetPowerReadingsIntervalRequest request, StreamObserver<com.iots.grpc.power.PowerAggregate> responseObserver) {
        LOGGER.info("getPowerAggregatesForTimePeriod received: {}", request);
        com.iots.grpc.power.PowerAggregate aggregate = repository.getPowerAggregateForInterval(request);
        responseObserver.onNext(aggregate);
        responseObserver.onCompleted();
    }
}
