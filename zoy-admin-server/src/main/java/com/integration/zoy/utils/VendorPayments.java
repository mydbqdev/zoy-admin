package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class VendorPayments {
	@SerializedName("ownerId")
	private String ownerId;
	
	@SerializedName("ownerName")
	private String ownerName;
	
	@SerializedName("pgId")
	private String pgId;
	
	@SerializedName("pgName")
	private String pgName;
	
	@SerializedName("totalAmountFromTenants")
	private BigDecimal totalAmountFromTenants;
	
	@SerializedName("amountPaidToOwner")
	private BigDecimal amountPaidToOwner;
	
	@SerializedName("zoyCommission")
	private BigDecimal zoyCommission;
	
	@SerializedName("transactionDate")
	private Timestamp transactionDate;
	
	@SerializedName("transactionNumber")
	private String transactionNumber;
	
	@SerializedName("paymentStatus")
	private String paymentStatus;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPgId() {
		return pgId;
	}

	public void setPgId(String pgId) {
		this.pgId = pgId;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	

	public BigDecimal getTotalAmountFromTenants() {
		return totalAmountFromTenants;
	}

	public void setTotalAmountFromTenants(BigDecimal totalAmountFromTenants) {
		this.totalAmountFromTenants = totalAmountFromTenants;
	}

	public BigDecimal getAmountPaidToOwner() {
		return amountPaidToOwner;
	}

	public void setAmountPaidToOwner(BigDecimal amountPaidToOwner) {
		this.amountPaidToOwner = amountPaidToOwner;
	}

	public BigDecimal getZoyCommission() {
		return zoyCommission;
	}

	public void setZoyCommission(BigDecimal zoyCommission) {
		this.zoyCommission = zoyCommission;
	}

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

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	
	
}
