package com.integration.zoy.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String status;
    private T data;
}
