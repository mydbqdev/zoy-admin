package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyGstChargesDto {
	@SerializedName("rentId")
	private String rentId;

	@SerializedName("cgstPercentage")
	private BigDecimal cgstPercentage;
	
	@SerializedName("sgstPercentage")
	private BigDecimal sgstPercentage;
	
	@SerializedName("igstPercentage")
	private BigDecimal igstPercentage;
	
	@SerializedName("monthlyRent")
	private BigDecimal monthlyRent;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;
	
	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

	public String getRentId() {
		return rentId;
	}

	public void setRentId(String rentId) {
		this.rentId = rentId;
	}

	

	public BigDecimal getCgstPercentage() {
		return cgstPercentage;
	}

	public void setCgstPercentage(BigDecimal cgstPercentage) {
		this.cgstPercentage = cgstPercentage;
	}

	public BigDecimal getSgstPercentage() {
		return sgstPercentage;
	}

	public void setSgstPercentage(BigDecimal sgstPercentage) {
		this.sgstPercentage = sgstPercentage;
	}

	public BigDecimal getIgstPercentage() {
		return igstPercentage;
	}

	public void setIgstPercentage(BigDecimal igstPercentage) {
		this.igstPercentage = igstPercentage;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	
}
