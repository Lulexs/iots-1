package com.iots.DataManager.repository;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Util {
    public static LocalDateTime toLocalDateTime(Timestamp protoTimestamp) {
        if (protoTimestamp == null) return null;
        Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    public static Timestamp toProtoTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        Instant instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
