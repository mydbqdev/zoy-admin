package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AdminUserList {
	@SerializedName("first_name")
	String firstName;

	@SerializedName("last_name")
	String lastName;

	@SerializedName("user_email")
	String userEmail;

	@SerializedName("contact_number")
	String contactNumber;

	@SerializedName("designation")
	String designation;

	@SerializedName("status")
	String status;

	@SerializedName("approved_privilege")
	List<String> approvedPrivilege;

	@SerializedName("unapproved_privilege")
	List<String> unapprovedPrivilege;


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

	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}

	public void setApprovedPrivilege(List<String> approvedPrivilege) {
		this.approvedPrivilege = approvedPrivilege;
	}
	public List<String> getApprovedPrivilege() {
		return approvedPrivilege;
	}

	public void setUnapprovedPrivilege(List<String> unapprovedPrivilege) {
		this.unapprovedPrivilege = unapprovedPrivilege;
	}
	public List<String> getUnapprovedPrivilege() {
		return unapprovedPrivilege;
	}
}
