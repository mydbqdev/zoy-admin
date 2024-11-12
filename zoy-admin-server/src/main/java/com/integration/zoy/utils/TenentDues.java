package com.integration.zoy.utils;



import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class TenentDues {
	
	@SerializedName("customerId")
	private String userId;
	
	@SerializedName("customerName")
    private String userPersonalName;
	
	@SerializedName("PgPropertyName")
    private String userPgPropertyName;
	
	@SerializedName("PgPropertyId")
    private String userPgPropertyId;
	
	@SerializedName("bedNumber")
	private String bedNumber;
	
	@SerializedName("pendingAmount")
	private BigDecimal pendingAmount;
	
	@SerializedName("pendingDueDate")
	private Timestamp pendingDueDate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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

	public String getUserPgPropertyId() {
		return userPgPropertyId;
	}

	public void setUserPgPropertyId(String userPgPropertyId) {
		this.userPgPropertyId = userPgPropertyId;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public Timestamp getPendingDueDate() {
		return pendingDueDate;
	}

	public void setPendingDueDate(Timestamp pendingDueDate) {
		this.pendingDueDate = pendingDueDate;
	}
	
	
	

}
