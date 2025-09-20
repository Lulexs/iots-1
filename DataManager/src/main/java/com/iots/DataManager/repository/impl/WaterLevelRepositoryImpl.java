package com.iots.DataManager.repository.impl;

import com.iots.DataManager.repository.Util;
import com.iots.DataManager.repository.WaterLevelRepository;
import com.iots.grpc.watertank.WaterTankWaterLevelReading;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.iots.DataManager.jooq.tables.WaterLevel.WATER_LEVEL;

@Repository
public class WaterLevelRepositoryImpl implements WaterLevelRepository {

    private final DSLContext dsl;

    public WaterLevelRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void insertWaterLevelReading(WaterTankWaterLevelReading waterLevelReading) {
        dsl.insertInto(WATER_LEVEL)
                .set(WATER_LEVEL.WATER_TANK, waterLevelReading.getWaterTankName())
                .set(WATER_LEVEL.WATER_LEVEL_, waterLevelReading.getWaterLevel())
                .set(WATER_LEVEL.READING_TIME, Util.toLocalDateTime(waterLevelReading.getReadingTime()))
                .execute();
    }
}
