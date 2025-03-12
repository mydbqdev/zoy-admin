package com.integration.zoy.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class TimestampFormatterUtilService {
	private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String TIME_ZONE = "Asia/Kolkata";

    public  String formatTimestamp(Instant timestamp) {
        ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.of(TIME_ZONE));
        return zonedDateTime.format(INPUT_FORMATTER);
    }
    
    public  String convertToDateOnly(String dateTimeString) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, INPUT_FORMATTER);
        return dateTime.format(OUTPUT_FORMATTER);
    }
}
