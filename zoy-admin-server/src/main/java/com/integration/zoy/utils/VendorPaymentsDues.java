package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class VendorPaymentsDues {
	@SerializedName("ownerId")
	private String ownerId;
	
	@SerializedName("ownerName")
	private String ownerName;
	
	@SerializedName("pgId")
	private String pgId;
	
	@SerializedName("pgName")
	private String pgName;
	
	@SerializedName("totalAmountPayable")
	private BigDecimal totalAmountPayable;
	
	@SerializedName("totalAmountPaid")
	private BigDecimal totalAmountPaid;
	
	@SerializedName("pendingAmount")
	private BigDecimal pendingAmount;
	
	@SerializedName("pendingDueDate")
	private Timestamp pendingDueDate;

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

	public BigDecimal getTotalAmountPayable() {
		return totalAmountPayable;
	}

	public void setTotalAmountPayable(BigDecimal totalAmountPayable) {
		this.totalAmountPayable = totalAmountPayable;
	}

	public BigDecimal getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
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
