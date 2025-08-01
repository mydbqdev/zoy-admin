package com.integration.zoy.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class VendorResponseDto {

    private String vendorId;
    private String vendorCompanyName;
    private String vendorFirstName;
    private String vendorLastName;
    private String vendorAddress;
    private String vendorMobileNo;
    private String vendorAlternativeNo;
    private String vendorEmail;
    private String vendorGstRegistrastionNo;
    private String vendorStatus;
    private Timestamp vendorCreatedAt;
    private List<VendorServiceDto> services;
    private List<String> vendorFilePaths;

    @Data
    public static class VendorServiceDto {
        private String vendorServiceId;
        private String serviceName;
    }
}
