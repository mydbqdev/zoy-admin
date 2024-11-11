package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;


public class AdminUserDetails {

	@SerializedName("firstName")
	String firstName;

	@SerializedName("lastName")
	String lastName;

	@SerializedName("mobileNumber")
	String mobileNumber;

	@SerializedName("userEmail")
	String userEmail;

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

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserEmail() {
		return userEmail;
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