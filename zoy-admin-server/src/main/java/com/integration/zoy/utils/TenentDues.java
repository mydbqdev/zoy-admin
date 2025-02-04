package com.integration.zoy.utils;



import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class TenentDues {
	
	@SerializedName("customerName")
    private String userPersonalName;
	
	@SerializedName("tenantMobileNum")
	private String tenantMobileNum;
	
	@SerializedName("PgPropertyName")
    private String userPgPropertyName;
	
	@SerializedName("bedNumber")
	private String bedNumber;
	
	@SerializedName("pendingAmount")
	private String pendingAmount;
	
	@SerializedName("pendingDueDate")
	private Timestamp pendingDueDate;
	
	@SerializedName("userPgPropertyAddress")
	private String userPgPropertyAddress;


	public String getUserPersonalName() {
		return userPersonalName;
	}

	public void setUserPersonalName(String userPersonalName) {
		this.userPersonalName = userPersonalName;
	}

	public String getUserPgPropertyName() {
		return userPgPropertyName;
	}

	public void setUserPgPropertyName(String userPgPropertyName) {
		this.userPgPropertyName = userPgPropertyName;
	}


	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public String getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(String pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public Timestamp getPendingDueDate() {
		return pendingDueDate;
	}

	public void setPendingDueDate(Timestamp pendingDueDate) {
		this.pendingDueDate = pendingDueDate;
	}

	public String getUserPgPropertyAddress() {
		return userPgPropertyAddress;
	}

	public void setUserPgPropertyAddress(String userPgPropertyAddress) {
		this.userPgPropertyAddress = userPgPropertyAddress;
	}

	public String getTenantMobileNum() {
		return tenantMobileNum;
	}

	public void setTenantMobileNum(String tenantMobileNum) {
		this.tenantMobileNum = tenantMobileNum;
	}
	
	
	

}
