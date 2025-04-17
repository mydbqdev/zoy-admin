package com.integration.zoy.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

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

	public String currentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
		Date date = new Date();
		String currentDate=dateFormat.format(date);
		return currentDate;
	}
}
