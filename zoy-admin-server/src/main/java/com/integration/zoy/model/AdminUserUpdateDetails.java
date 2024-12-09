package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;


public class AdminUserUpdateDetails {

	@SerializedName("first_name")
	String firstName;

	@SerializedName("last_name")
	String lastName;

	@SerializedName("designation")
	String designation;

	@SerializedName("contactNumber")
	String contactNumber;

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

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDesignation() {
		return designation;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}