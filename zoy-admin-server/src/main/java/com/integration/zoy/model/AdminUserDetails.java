package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;


public class AdminUserDetails {

	@SerializedName("firstName")
	String firstName;

	@SerializedName("lastName")
	String lastName;

	@SerializedName("contactNumber")
	String contactNumber;

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

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getContactNumberr() {
		return contactNumber;
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