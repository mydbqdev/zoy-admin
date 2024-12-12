package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class VendorPayments {
	
	@SerializedName("ownerName")
	private String ownerName;
	
	@SerializedName("ownerEmail")
	private String ownerEmail;
	
	@SerializedName("pgName")
	private String pgName;
	
	@SerializedName("pgAddress")
	private String pgAddress;
	
	@SerializedName("totalAmountFromTenants")
	private BigDecimal totalAmountFromTenants;
	
	@SerializedName("amountPaidToOwner")
	private BigDecimal amountPaidToOwner;
	
	@SerializedName("zoyShare")
	private BigDecimal zoyShare;
	
	@SerializedName("transactionDate")
	private Timestamp transactionDate;
	
	@SerializedName("transactionNumber")
	private String transactionNumber;
	
	@SerializedName("paymentStatus")
	private String paymentStatus;
	
	@SerializedName("ownerApprovalStatus")
	private String ownerApprovalStatus;


	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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

	public String getPgAddress() {
		return pgAddress;
	}

	public void setPgAddress(String pgAddress) {
		this.pgAddress = pgAddress;
	}

	public BigDecimal getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(BigDecimal zoyShare) {
		this.zoyShare = zoyShare;
	}

	public String getOwnerApprovalStatus() {
		return ownerApprovalStatus;
	}

	public void setOwnerApprovalStatus(String ownerApprovalStatus) {
		this.ownerApprovalStatus = ownerApprovalStatus;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	
	
}
