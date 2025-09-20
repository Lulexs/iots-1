package com.iots.DataManager.service;

import com.iots.DataManager.repository.WaterLevelRepository;
import com.iots.grpc.watertank.WaterTankServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class WaterLevelService extends WaterTankServiceGrpc.WaterTankServiceImplBase {

    private final WaterLevelRepository waterLevelRepository;

    public WaterLevelService(WaterLevelRepository waterLevelRepository) {
        this.waterLevelRepository = waterLevelRepository;
    }

    @Override
    public void registerReading(com.iots.grpc.watertank.WaterTankWaterLevelReading waterLevelReading,
                                StreamObserver<com.iots.grpc.watertank.EmptyResponse> responseObserver) {
        waterLevelRepository.insertWaterLevelReading(waterLevelReading);
        responseObserver.onNext(com.iots.grpc.watertank.EmptyResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
