package com.integration.zoy.service;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Iso8601ToTimestampConverter extends AbstractBeanField<String, Timestamp> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected Timestamp convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
        	System.out.println(value);
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return new Timestamp(DATE_FORMAT.parse(value).getTime());
        } catch (ParseException e) {
            throw new CsvDataTypeMismatchException(value, Timestamp.class, "Invalid date format: " + e.getMessage());
        }
    }
}

