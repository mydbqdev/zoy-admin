package com.integration.zoy.utils;

import java.util.List;

import lombok.Data;

@Data
public class Role {
    private String id;
    private String name;
    private String description;
    private List<String> appRoleId;
}