package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AdminUserDetailPrevilage {
	@SerializedName("firstName")
	String firstName;

	@SerializedName("lastName")
	String lastName;

	@SerializedName("userEmail")
	String userEmail;

	@SerializedName("contactNumber")
	String contactNumber;

	@SerializedName("designation")
	String designation;

	@SerializedName("privilege")
	List<String> privilege;

	@SerializedName("token")
	String token;


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

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserEmail() {
		return userEmail;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getContactNumber() {
		return contactNumber;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDesignation() {
		return designation;
	}
	public List<String> getPrivilege() {
		return privilege;
	}
	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	
}
