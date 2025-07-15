package com.integration.zoy.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ZoyPgSalesMasterModel {

	@SerializedName("emailId")
	private String emailId;

	@SerializedName("employeeId")
	private String employeeId;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("middleName")
	private String middleName;

	@SerializedName("mobileNo")
	private String mobileNo;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("fullName")
	private String fullName;

	@SerializedName("isPassWordChange")
	private Boolean isPassWordChange;

	@SerializedName("userDesignation")
	private String userDesignation;

	@SerializedName("userGroupId")
	private String userGroupId;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Boolean getIsPassWordChange() {
		return isPassWordChange;
	}

	public void setIsPassWordChange(Boolean isPassWordChange) {
		this.isPassWordChange = isPassWordChange;
	}

	public String getUserDesignation() {
		return userDesignation;
	}

	public void setUserDesignation(String userDesignation) {
		this.userDesignation = userDesignation;
	}

	public String getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}

	
}
