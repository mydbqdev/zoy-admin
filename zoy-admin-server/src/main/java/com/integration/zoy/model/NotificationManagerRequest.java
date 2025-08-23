package com.integration.zoy.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NotificationManagerRequest {

    private String email;
    private String mergeInfo;
    private String templateId;
    private String mobile;
    private MultipartFile file = null; 
    private List<String> token =null;

    
}
