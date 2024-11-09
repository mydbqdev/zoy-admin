package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;


public class AdminUserUpdateDetails {

	@SerializedName("first_name")
	String firstName;

	@SerializedName("last_name")
	String lastName;

	@SerializedName("mobile_number")
	String mobileNumber;

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

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDesignation() {
		return designation;
	}

}