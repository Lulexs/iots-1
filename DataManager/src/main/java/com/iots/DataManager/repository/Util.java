package com.iots.DataManager.repository;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Util {
    public static LocalDateTime toLocalDateTime(Timestamp protoTimestamp) {
        if (protoTimestamp == null) return null;

        Instant instant = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
