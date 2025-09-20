package com.iots.DataManager.repository;

import com.iots.grpc.watertank.WaterTankWaterLevelReading;
import com.iots.grpc.watertank.DeleteWaterTankWaterLevelReadingDto;
import com.iots.grpc.watertank.WaterTankWaterLevelAggregate;
import com.iots.grpc.watertank.GetReadingsIntervalRequest;
import com.iots.grpc.watertank.GetReadingRequest;

import java.util.List;

public interface WaterLevelRepository {

    void insertWaterLevelReading(WaterTankWaterLevelReading reading);
    void updateWaterLevelReading(WaterTankWaterLevelReading reading);
    void deleteWaterLevelReading(DeleteWaterTankWaterLevelReadingDto dto);
    WaterTankWaterLevelReading getWaterLevelReadingAtTime(GetReadingRequest request);
    List<WaterTankWaterLevelReading> getWaterLevelReadingsInInterval(GetReadingsIntervalRequest request);
    WaterTankWaterLevelAggregate getWaterLevelAggregateForInterval(GetReadingsIntervalRequest request);
}