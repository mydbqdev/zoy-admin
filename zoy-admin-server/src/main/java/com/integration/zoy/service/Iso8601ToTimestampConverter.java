package com.integration.zoy.service;

import com.opencsv.bean.AbstractBeanField;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Iso8601ToTimestampConverter extends AbstractBeanField<String, Timestamp> {

    private static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    protected Timestamp convert(String value) {
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat(ISO_8601_FORMAT);
            return new Timestamp(iso8601Format.parse(value).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing ISO 8601 date: " + value, e);
        }
    }
}

