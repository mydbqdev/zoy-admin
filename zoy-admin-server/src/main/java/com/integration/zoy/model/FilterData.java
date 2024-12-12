package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class FilterData {
	@SerializedName("tenantName")
	String tenantName;

	@SerializedName("transactionStatus")
	String transactionStatus;

	@SerializedName("modeOfPayment")
	String modeOfPayment;

	@SerializedName("zoyCode")
	String zoyCode;

	@SerializedName("ownerName")
	String ownerName;

	@SerializedName("pgName")
	String pgName;

	@SerializedName("payeeType")
	String payeeType;

	@SerializedName("payeeName")
	String payeeName;

	@SerializedName("payerType")
	String payerType;

	@SerializedName("payerName")
	String payerName;
	
	@SerializedName("tenantContactNum")
	String tenantContactNum;
	
	@SerializedName("ownerEmail")
	String ownerEmail;

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getTenantName() {
		return tenantName;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}
	public String getZoyCode() {
		return zoyCode;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	public String getPgName() {
		return pgName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeName() {
		return payeeName;
	}

	public String getPayeeType() {
		return payeeType;
	}
	public void setPayeeType(String payeeType) {
		this.payeeType = payeeType;
	}
	public String getPayerType() {
		return payerType;
	}
	public void setPayerType(String payerType) {
		this.payerType = payerType;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getPayerName() {
		return payerName;
	}
	public String getTenantContactNum() {
		return tenantContactNum;
	}
	public void setTenantContactNum(String tenantContactNum) {
		this.tenantContactNum = tenantContactNum;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	
}
