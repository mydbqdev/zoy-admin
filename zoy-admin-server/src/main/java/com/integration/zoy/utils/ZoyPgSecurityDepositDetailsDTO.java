package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyPgSecurityDepositDetailsDTO {

	@SerializedName("depositId")
	private String depositId;

	@SerializedName("minimumDeposit")
	private BigDecimal minimumDeposit;

	@SerializedName("maximumDeposit")
	private BigDecimal maximumDeposit;

	@SerializedName("effectiveDate")
	private String effectiveDate;

	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;
	
	@SerializedName("comments")
	private String comments;

	public String getDepositId() {
		return depositId;
	}

	public void setDepositId(String depositId) {
		this.depositId = depositId;
	}

	public BigDecimal getMinimumDeposit() {
		return minimumDeposit;
	}

	public void setMinimumDeposit(BigDecimal minimumDeposit) {
		this.minimumDeposit = minimumDeposit;
	}

	public BigDecimal getMaximumDeposit() {
		return maximumDeposit;
	}

	public void setMaximumDeposit(BigDecimal maximumDeposit) {
		this.maximumDeposit = maximumDeposit;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public void setApproved(boolean isApproved) {
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
