package com.iots.DataManager.repository;

import com.iots.grpc.power.PowerReading;
import com.iots.grpc.power.DeletePowerReadingDto;
import com.iots.grpc.power.GetPowerReadingRequest;
import com.iots.grpc.power.GetPowerReadingsIntervalRequest;
import com.iots.grpc.power.PowerAggregate;

import java.util.List;

public interface PowerRepository {

    void insertPowerReading(PowerReading powerReading);
    void updatePowerReading(PowerReading powerReading);
    void deletePowerReading(DeletePowerReadingDto dto);
    PowerReading getPowerReadingAtTime(GetPowerReadingRequest request);
    List<PowerReading> getPowerReadingsInInterval(GetPowerReadingsIntervalRequest request);
    PowerAggregate getPowerAggregateForInterval(GetPowerReadingsIntervalRequest request);
}
