package com.iots.DataManager.service;

import com.iots.DataManager.repository.WaterLevelRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@GrpcService
public class WaterLevelService extends com.iots.grpc.watertank.WaterTankServiceGrpc.WaterTankServiceImplBase {

    private final WaterLevelRepository waterLevelRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterLevelService.class);

    public WaterLevelService(WaterLevelRepository waterLevelRepository) {
        this.waterLevelRepository = waterLevelRepository;
    }

    @Override
    @Transactional
    public void registerReading(com.iots.grpc.watertank.WaterTankWaterLevelReading reading, StreamObserver<Empty> responseObserver) {
        LOGGER.info("registerReading received: {}", reading);
        waterLevelRepository.insertWaterLevelReading(reading);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void updateReading(com.iots.grpc.watertank.WaterTankWaterLevelReading reading, StreamObserver<Empty> responseObserver) {
        LOGGER.info("updateReading received: {}", reading);
        waterLevelRepository.updateWaterLevelReading(reading);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void deleteReading(com.iots.grpc.watertank.DeleteWaterTankWaterLevelReadingDto dto, StreamObserver<Empty> responseObserver) {
        LOGGER.info("deleteReading received: {}", dto);
        waterLevelRepository.deleteWaterLevelReading(dto);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getWaterTankWaterLevelReadingAtTime(com.iots.grpc.watertank.GetReadingRequest request,
                                                    StreamObserver<com.iots.grpc.watertank.WaterTankWaterLevelReading> responseObserver) {
        LOGGER.info("getWaterTankWaterLevelReadingAtTime received: {}", request);
        com.iots.grpc.watertank.WaterTankWaterLevelReading reading = waterLevelRepository.getWaterLevelReadingAtTime(request);
        if (reading != null) {
            responseObserver.onNext(reading);
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getWaterTankWaterLevelReadingsInInterval(com.iots.grpc.watertank.GetReadingsIntervalRequest request,
                                                         StreamObserver<com.iots.grpc.watertank.WaterTankWaterLevelReadingsResponse> responseObserver) {
        LOGGER.info("getWaterTankWaterLevelReadingsInInterval received: {}", request);
        List<com.iots.grpc.watertank.WaterTankWaterLevelReading> readings = waterLevelRepository.getWaterLevelReadingsInInterval(request);
        com.iots.grpc.watertank.WaterTankWaterLevelReadingsResponse response = com.iots.grpc.watertank.WaterTankWaterLevelReadingsResponse.newBuilder()
                .addAllReadings(readings)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getWaterTankWaterLevelReadingAggregatesForTimePeriod(com.iots.grpc.watertank.GetReadingsIntervalRequest request,
                                                                     StreamObserver<com.iots.grpc.watertank.WaterTankWaterLevelAggregate> responseObserver) {
        LOGGER.info("getWaterTankWaterLevelReadingAggregatesForTimePeriod received: {}", request);
        com.iots.grpc.watertank.WaterTankWaterLevelAggregate aggregate = waterLevelRepository.getWaterLevelAggregateForInterval(request);
        responseObserver.onNext(aggregate);
        responseObserver.onCompleted();
    }
}
