package com.integration.zoy.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupResponse {
	private String id;
	private String name;
	private String description;
}
