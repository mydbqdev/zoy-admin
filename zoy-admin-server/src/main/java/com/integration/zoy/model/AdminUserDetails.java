package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;


public class AdminUserDetails {

	@SerializedName("first_name")
	String firstName;

	@SerializedName("last_name")
	String lastName;

	@SerializedName("mobile_number")
	String mobileNumber;

	@SerializedName("email_id")
	String emailId;

	@SerializedName("password")
	String password;

	@SerializedName("designation")
	String designation;


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

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getEmailId() {
		return emailId;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDesignation() {
		return designation;
	}

}