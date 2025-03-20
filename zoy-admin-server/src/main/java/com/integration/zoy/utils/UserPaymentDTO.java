package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class UserPaymentDTO {

	@SerializedName("transactionDate")
	private Timestamp transactionDate;

	@SerializedName("transactionNumber")
	private String transactionNumber;

	@SerializedName("transactionStatus")
	private String transactionStatus;

	@SerializedName("baseAmount")
	private double dueAmount;

	@SerializedName("gstAmount")
	private double gstAmount;

	@SerializedName("totalAmount")
	private double totalAmount;

	@SerializedName("customerName")
	private String userPersonalName;

	@SerializedName("PgPropertyName")
	private String userPgPropertyName;

	@SerializedName("propertyId")
	private String propertyId;

	@SerializedName("bedNumber")
	private String roomBedNumber;

	@SerializedName("category")
	private String category;

	@SerializedName("paymentMethod")
	private String paymentMode;

	@SerializedName("propertyHouseArea")
	private String propertyHouseArea;

	@SerializedName("tenantContactNum")
	private String tenantContactNum;
	
	@SerializedName("failedReason")
	private String failedReason;
	
	@SerializedName("email")
	private String email;

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
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

	public String getRoomBedNumber() {
		return roomBedNumber;
	}

	public void setRoomBedNumber(String roomBedNumber) {
		this.roomBedNumber = roomBedNumber;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPropertyHouseArea() {
		return propertyHouseArea;
	}

	public void setPropertyHouseArea(String propertyHouseArea) {
		this.propertyHouseArea = propertyHouseArea;
	}

	public String getTenantContactNum() {
		return tenantContactNum;
	}

	public void setTenantContactNum(String tenantContactNum) {
		this.tenantContactNum = tenantContactNum;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
