package com.iots.DataManager.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.iots.DataManager.publisher.MqttPublisher;
import com.iots.DataManager.repository.WaterLevelRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

import java.util.HashSet;
import java.util.List;

@GrpcService
public class WaterLevelService extends com.iots.grpc.watertank.WaterTankServiceGrpc.WaterTankServiceImplBase {

    private final WaterLevelRepository waterLevelRepository;
    private final MqttPublisher mqttPublisher;
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterLevelService.class);

    public WaterLevelService(WaterLevelRepository waterLevelRepository,
                             MqttPublisher mqttPublisher) {
        this.waterLevelRepository = waterLevelRepository;
        this.mqttPublisher = mqttPublisher;
    }

    @Override
    public void registerReading(com.iots.grpc.watertank.WaterTankWaterLevelReading reading, StreamObserver<Empty> responseObserver) {
        LOGGER.info("registerReading received: {}", reading);
        try {
            waterLevelRepository.insertWaterLevelReading(reading);
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Failed to register reading: {}", reading);
        }

        try {
            String serialized = JsonFormat.printer()
                    .includingDefaultValueFields(new HashSet<>(reading.getDescriptorForType().getFields()))
                    .print(reading);

            String withClassName = String.format(
                    "{\"type\":\"%s\", \"data\":%s}",
                    reading.getDescriptorForType().getName(),
                    serialized
            );
            mqttPublisher.publish("dev/cdc", withClassName);
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("Failed to write to mqtt topic because of json processing exception", e);
        }
    }

    @Override
    public void updateReading(com.iots.grpc.watertank.WaterTankWaterLevelReading reading, StreamObserver<Empty> responseObserver) {
        LOGGER.info("updateReading received: {}", reading);
        waterLevelRepository.updateWaterLevelReading(reading);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteReading(com.iots.grpc.watertank.DeleteWaterTankWaterLevelReadingDto dto, StreamObserver<Empty> responseObserver) {
        LOGGER.info("deleteReading received: {}", dto);
        waterLevelRepository.deleteWaterLevelReading(dto);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
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
    public void getWaterTankWaterLevelReadingAggregatesForTimePeriod(com.iots.grpc.watertank.GetReadingsIntervalRequest request,
                                                                     StreamObserver<com.iots.grpc.watertank.WaterTankWaterLevelAggregate> responseObserver) {
        LOGGER.info("getWaterTankWaterLevelReadingAggregatesForTimePeriod received: {}", request);
        com.iots.grpc.watertank.WaterTankWaterLevelAggregate aggregate = waterLevelRepository.getWaterLevelAggregateForInterval(request);
        responseObserver.onNext(aggregate);
        responseObserver.onCompleted();
    }
}
