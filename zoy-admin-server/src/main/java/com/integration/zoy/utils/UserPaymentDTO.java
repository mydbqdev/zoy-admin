package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class UserPaymentDTO {
	
	@SerializedName("transactionDate")
    private Timestamp userPaymentTimestamp;
	
	@SerializedName("transactionNumber")
    private String userPaymentBankTransactionId;
	
	@SerializedName("transactionStatus")
    private String userPaymentResultStatus;
	
	@SerializedName("baseAmount")
    private BigDecimal userPaymentPayableAmount;
	
	@SerializedName("gstAmount")
    private BigDecimal userPaymentGst;
	
	@SerializedName("totalAmount")
	private BigDecimal totalAmount;
	
	@SerializedName("customerName")
    private String userPersonalName;
	
	@SerializedName("PgPropertyName")
    private String userPgPropertyName;
	
	@SerializedName("bedNumber")
	private String bedNumber;
	
	@SerializedName("category")
	private String category;
	
	@SerializedName("paymentMethod")
	private String paymentMethod;
	
	@SerializedName("propertyHouseArea")
	private String propertyHouseArea;
	
	@SerializedName("tenantContactNum")
	private String tenantContactNum;
	
	public Timestamp getUserPaymentTimestamp() {
		return userPaymentTimestamp;
	}
	public void setUserPaymentTimestamp(Timestamp userPaymentTimestamp) {
		this.userPaymentTimestamp = userPaymentTimestamp;
	}
	public String getUserPaymentBankTransactionId() {
		return userPaymentBankTransactionId;
	}
	public void setUserPaymentBankTransactionId(String userPaymentBankTransactionId) {
		this.userPaymentBankTransactionId = userPaymentBankTransactionId;
	}
	public String getUserPaymentResultStatus() {
		return userPaymentResultStatus;
	}
	public void setUserPaymentResultStatus(String userPaymentResultStatus) {
		this.userPaymentResultStatus = userPaymentResultStatus;
	}

	public BigDecimal getUserPaymentPayableAmount() {
		return userPaymentPayableAmount;
	}
	public void setUserPaymentPayableAmount(BigDecimal userPaymentPayableAmount) {
		this.userPaymentPayableAmount = userPaymentPayableAmount;
	}
	public BigDecimal getUserPaymentGst() {
		return userPaymentGst;
	}
	public void setUserPaymentGst(BigDecimal userPaymentGst) {
		this.userPaymentGst = userPaymentGst;
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
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getBedNumber() {
		return bedNumber;
	}
	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
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
	
	

}
