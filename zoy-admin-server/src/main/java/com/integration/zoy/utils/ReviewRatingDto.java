package com.integration.zoy.utils;


import lombok.Data;

@Data
public class ReviewRatingDto {
    private String tenantName;
    private String tenantMobileNo;
    private String pgName;
    private double star;
}
