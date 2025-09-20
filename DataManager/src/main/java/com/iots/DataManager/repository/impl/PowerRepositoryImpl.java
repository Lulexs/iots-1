package com.iots.DataManager.repository.impl;

import com.iots.DataManager.repository.PowerRepository;
import com.iots.DataManager.repository.Util;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.iots.DataManager.jooq.tables.PowerData.POWER_DATA;

@Repository
public class PowerRepositoryImpl implements PowerRepository {

    private final DSLContext dsl;

    public PowerRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public void insertPowerReading(com.iots.grpc.power.PowerReading powerReading) {
        dsl.insertInto(POWER_DATA)
                .set(POWER_DATA.WATER_TANK, powerReading.getWaterTank())
                .set(POWER_DATA.PUMP, powerReading.getPump())
                .set(POWER_DATA.READING_TIME, Util.toLocalDateTime(powerReading.getReadingTime()))
                .set(POWER_DATA.CHANNEL1_POWER, powerReading.getChannel1Power())
                .set(POWER_DATA.CHANNEL2_POWER, powerReading.getChannel2Power())
                .set(POWER_DATA.CHANNEL3_POWER, powerReading.getChannel3Power())
                .execute();
    }

    @Override
    @Transactional
    public void updatePowerReading(com.iots.grpc.power.PowerReading powerReading) {
        LocalDateTime ts = Util.toLocalDateTime(powerReading.getReadingTime());
        dsl.update(POWER_DATA)
                .set(POWER_DATA.CHANNEL1_POWER, powerReading.getChannel1Power())
                .set(POWER_DATA.CHANNEL2_POWER, powerReading.getChannel2Power())
                .set(POWER_DATA.CHANNEL3_POWER, powerReading.getChannel3Power())
                .where(POWER_DATA.WATER_TANK.eq(powerReading.getWaterTank()))
                .and(POWER_DATA.PUMP.eq(powerReading.getPump()))
                .and(POWER_DATA.READING_TIME.between(ts.minusSeconds(1), ts.plusSeconds(1)))
                .execute();
    }

    @Override
    @Transactional
    public void deletePowerReading(com.iots.grpc.power.DeletePowerReadingDto dto) {
        dsl.deleteFrom(POWER_DATA)
                .where(POWER_DATA.WATER_TANK.eq(dto.getWaterTank()))
                .and(POWER_DATA.PUMP.eq(dto.getPump()))
                .and(POWER_DATA.READING_TIME.eq(Util.toLocalDateTime(dto.getReadingTime())))
                .execute();
    }

    @Override
    @Transactional
    public com.iots.grpc.power.PowerReading getPowerReadingAtTime(com.iots.grpc.power.GetPowerReadingRequest request) {
        return dsl.selectFrom(POWER_DATA)
                .where(POWER_DATA.WATER_TANK.eq(request.getWaterTank()))
                .and(POWER_DATA.PUMP.eq(request.getPump()))
                .and(POWER_DATA.READING_TIME.le(Util.toLocalDateTime(request.getReadingTime())))
                .orderBy(POWER_DATA.READING_TIME.desc())
                .limit(1)
                .fetchOptional()
                .map(record -> com.iots.grpc.power.PowerReading.newBuilder()
                        .setWaterTank(record.getWaterTank())
                        .setPump(record.getPump())
                        .setChannel1Power(record.getChannel1Power())
                        .setChannel2Power(record.getChannel2Power())
                        .setChannel3Power(record.getChannel3Power())
                        .setReadingTime(Util.toProtoTimestamp(record.getReadingTime()))
                        .build())
                .orElse(null);
    }

    @Override
    @Transactional
    public List<com.iots.grpc.power.PowerReading> getPowerReadingsInInterval(com.iots.grpc.power.GetPowerReadingsIntervalRequest request) {
        return dsl.selectFrom(POWER_DATA)
                .where(POWER_DATA.WATER_TANK.eq(request.getWaterTank()))
                .and(POWER_DATA.PUMP.eq(request.getPump()))
                .and(POWER_DATA.READING_TIME.between(
                        Util.toLocalDateTime(request.getStartTime()),
                        Util.toLocalDateTime(request.getEndTime())
                ))
                .orderBy(POWER_DATA.READING_TIME.asc())
                .fetch(record -> com.iots.grpc.power.PowerReading.newBuilder()
                        .setWaterTank(record.getWaterTank())
                        .setPump(record.getPump())
                        .setChannel1Power(record.getChannel1Power())
                        .setChannel2Power(record.getChannel2Power())
                        .setChannel3Power(record.getChannel3Power())
                        .setReadingTime(Util.toProtoTimestamp(record.getReadingTime()))
                        .build()
                );
    }

    @Override
    @Transactional
    public com.iots.grpc.power.PowerAggregate getPowerAggregateForInterval(
            com.iots.grpc.power.GetPowerReadingsIntervalRequest request) {

        // Fetch all channel1_power values in the interval
        var records = dsl.select(POWER_DATA.CHANNEL1_POWER)
                .from(POWER_DATA)
                .where(POWER_DATA.WATER_TANK.eq(request.getWaterTank()))
                .and(POWER_DATA.PUMP.eq(request.getPump()))
                .and(POWER_DATA.READING_TIME.between(
                        Util.toLocalDateTime(request.getStartTime()),
                        Util.toLocalDateTime(request.getEndTime())
                ))
                .and(POWER_DATA.CHANNEL1_POWER.ne(0.0))
                .fetch(POWER_DATA.CHANNEL1_POWER);

        if (records.isEmpty()) {
            return com.iots.grpc.power.PowerAggregate.newBuilder()
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

        return com.iots.grpc.power.PowerAggregate.newBuilder()
                .setMin(min)
                .setMax(max)
                .setAvg(avg)
                .setSum(sum)
                .build();
    }
}
