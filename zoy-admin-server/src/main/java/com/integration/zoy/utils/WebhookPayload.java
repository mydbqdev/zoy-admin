package com.integration.zoy.utils;


import lombok.Data;

@Data
public class WebhookPayload<T> {
    private String event;
    private String tenant;
    private T data;
}
