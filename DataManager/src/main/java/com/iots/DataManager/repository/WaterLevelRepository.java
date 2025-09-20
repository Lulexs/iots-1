package com.iots.DataManager.repository;

public interface WaterLevelRepository {
    void insertWaterLevelReading(com.iots.grpc.watertank.WaterTankWaterLevelReading waterLevelReading);
}
