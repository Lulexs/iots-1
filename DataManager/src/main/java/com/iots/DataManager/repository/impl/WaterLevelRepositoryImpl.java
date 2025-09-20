package com.iots.DataManager.repository.impl;

import com.iots.DataManager.repository.Util;
import com.iots.DataManager.repository.WaterLevelRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.iots.DataManager.jooq.tables.WaterLevel.WATER_LEVEL;

@Repository
public class WaterLevelRepositoryImpl implements WaterLevelRepository {

    private final DSLContext dsl;

    public WaterLevelRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public void insertWaterLevelReading(com.iots.grpc.watertank.WaterTankWaterLevelReading waterLevelReading) {
        dsl.insertInto(WATER_LEVEL)
                .set(WATER_LEVEL.WATER_TANK, waterLevelReading.getWaterTankName())
                .set(WATER_LEVEL.WATER_LEVEL_, waterLevelReading.getWaterLevel())
                .set(WATER_LEVEL.READING_TIME, Util.toLocalDateTime(waterLevelReading.getReadingTime()))
                .execute();
    }

    @Override
    @Transactional
    public void updateWaterLevelReading(com.iots.grpc.watertank.WaterTankWaterLevelReading reading) {
        dsl.update(WATER_LEVEL)
                .set(WATER_LEVEL.WATER_LEVEL_, reading.getWaterLevel())
                .where(WATER_LEVEL.WATER_TANK.eq(reading.getWaterTankName()))
                .and(WATER_LEVEL.READING_TIME.eq(Util.toLocalDateTime(reading.getReadingTime())))
                .execute();
    }

    @Override
    @Transactional
    public void deleteWaterLevelReading(com.iots.grpc.watertank.DeleteWaterTankWaterLevelReadingDto dto) {
        dsl.deleteFrom(WATER_LEVEL)
                .where(WATER_LEVEL.WATER_TANK.eq(dto.getWaterTank()))
                .and(WATER_LEVEL.READING_TIME.eq(Util.toLocalDateTime(dto.getReadingTime())))
                .execute();
    }

    @Override
    @Transactional
    public com.iots.grpc.watertank.WaterTankWaterLevelReading getWaterLevelReadingAtTime(com.iots.grpc.watertank.GetReadingRequest request) {
        return dsl.selectFrom(WATER_LEVEL)
                .where(WATER_LEVEL.WATER_TANK.eq(request.getWaterTank()))
                .and(WATER_LEVEL.READING_TIME.le(Util.toLocalDateTime(request.getReadingTime())))
                .orderBy(WATER_LEVEL.READING_TIME.desc())
                .limit(1)
                .fetchOptional()
                .map(record -> com.iots.grpc.watertank.WaterTankWaterLevelReading.newBuilder()
                        .setWaterTankName(record.getWaterTank())
                        .setWaterLevel(record.getWaterLevel())
                        .setReadingTime(Util.toProtoTimestamp(record.getReadingTime()))
                        .build())
                .orElse(null);
    }

    @Override
    @Transactional
    public List<com.iots.grpc.watertank.WaterTankWaterLevelReading> getWaterLevelReadingsInInterval(com.iots.grpc.watertank.GetReadingsIntervalRequest request) {
        return dsl.selectFrom(WATER_LEVEL)
                .where(WATER_LEVEL.WATER_TANK.eq(request.getWaterTank()))
                .and(WATER_LEVEL.READING_TIME.between(
                        Util.toLocalDateTime(request.getStartTime()),
                        Util.toLocalDateTime(request.getEndTime())))
                .fetch()
                .stream()
                .map(record -> com.iots.grpc.watertank.WaterTankWaterLevelReading.newBuilder()
                        .setWaterTankName(record.getWaterTank())
                        .setWaterLevel(record.getWaterLevel())
                        .setReadingTime(Util.toProtoTimestamp(record.getReadingTime()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public com.iots.grpc.watertank.WaterTankWaterLevelAggregate getWaterLevelAggregateForInterval(com.iots.grpc.watertank.GetReadingsIntervalRequest request) {
        var records = dsl.select(WATER_LEVEL.WATER_LEVEL_)
                .from(WATER_LEVEL)
                .where(WATER_LEVEL.WATER_TANK.eq(request.getWaterTank()))
                .and(WATER_LEVEL.READING_TIME.between(
                        Util.toLocalDateTime(request.getStartTime()),
                        Util.toLocalDateTime(request.getEndTime())))
                .and(WATER_LEVEL.WATER_LEVEL_.ne(0.0))
                .fetch(WATER_LEVEL.WATER_LEVEL_);

        if (records.isEmpty()) {
            return com.iots.grpc.watertank.WaterTankWaterLevelAggregate.newBuilder()
                    .setMin(0)
                    .setMax(0)
                    .setAvg(0)
                    .setSum(0)
                    .build();
        }

        double min = records.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = records.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double sum = records.stream().mapToDouble(Double::doubleValue).sum();
        double avg = sum / records.size();

        return com.iots.grpc.watertank.WaterTankWaterLevelAggregate.newBuilder()
                .setMin(min)
                .setMax(max)
                .setAvg(avg)
                .setSum(sum)
                .build();
    }
}
