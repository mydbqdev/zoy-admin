package com.integration.zoy.utils;

import java.sql.Timestamp;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMaster{
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNo;
	private Timestamp createdAt;
	private UserDesignation designation;
	private String userProfilePicture;
	private String initialPassword;

	@Data
	@NoArgsConstructor
	public static class UserDesignation {
		private String id;
		private String name;
		private String description;
	}
}