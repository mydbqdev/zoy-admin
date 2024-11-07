package com.integration.zoy.utils;

import java.sql.Timestamp;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ZoyCustomerDetails {
	@SerializedName("notificationMode")
	List<String> notificationMode;

	@SerializedName("mobile")
	String mobile;

	@SerializedName("email")
	String email;

	@SerializedName("registerDate")
	Timestamp registerDate;

	@SerializedName("firstName")
	String firstName;

	@SerializedName("lastName")
	String lastName;
	
	@SerializedName("userId")
	String userId;


	public void setNotificationMode(List<String> notificationMode) {
		this.notificationMode = notificationMode;
	}
	public List<String> getNotificationMode() {
		return notificationMode;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile() {
		return mobile;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}

	public void setRegisterDate(Timestamp registerDate) {
		this.registerDate = registerDate;
	}
	public Timestamp getRegisterDate() {
		return registerDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
