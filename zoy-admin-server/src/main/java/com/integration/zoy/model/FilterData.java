package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class FilterData {
	@SerializedName("tenantId")
	String tenantId;

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

	@SerializedName("pgId")
	String pgId;

	@SerializedName("pgName")
	String pgName;

	@SerializedName("payeeId")
	String payeeId;

	@SerializedName("payeeName")
	String payeeName;

	@SerializedName("payerId")
	String payerId;

	@SerializedName("payerName")
	String payerName;


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getTenantId() {
		return tenantId;
	}

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

	public void setPgId(String pgId) {
		this.pgId = pgId;
	}
	public String getPgId() {
		return pgId;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	public String getPgName() {
		return pgName;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}
	public String getPayeeId() {
		return payeeId;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeName() {
		return payeeName;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getPayerId() {
		return payerId;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getPayerName() {
		return payerName;
	}
}
