package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminLockedUsers {

	@JsonProperty("email")
	private String email;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("designation")
	private String designation;

	@JsonProperty("Status")
	private String Status;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

}
