package com.integration.zoy.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class UserRole {

	@JsonProperty("email_id")
	String emailId;

	@JsonProperty("role_id")
	List<Long> roleId;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public List<Long> getRoleId() {
		return roleId;
	}

	public void setRoleId(List<Long> roleId) {
		this.roleId = roleId;
	}



}