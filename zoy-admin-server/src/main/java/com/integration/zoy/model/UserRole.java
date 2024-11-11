package com.integration.zoy.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class UserRole {

	@JsonProperty("userEmail")
	String userEmail;

	@JsonProperty("roleId")
	List<Long> roleId;

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserEmail() {
		return userEmail;
	}

	public List<Long> getRoleId() {
		return roleId;
	}

	public void setRoleId(List<Long> roleId) {
		this.roleId = roleId;
	}



}