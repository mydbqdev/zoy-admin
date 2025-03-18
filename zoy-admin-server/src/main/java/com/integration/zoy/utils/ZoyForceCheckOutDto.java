package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyForceCheckOutDto {
	
	@SerializedName("forceCheckOutId")
	private String forceCheckOutId;

	@SerializedName("forceCheckOutDays")
	private int  forceCheckOutDays;

	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;
	
	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;
	
	public String getForceCheckOutId() {
		return forceCheckOutId;
	}

	public void setForceCheckOutId(String forceCheckOutId) {
		this.forceCheckOutId = forceCheckOutId;
	}

	public int getForceCheckOutDays() {
		return forceCheckOutDays;
	}

	public void setForceCheckOutDays(int forceCheckOutDays) {
		this.forceCheckOutDays = forceCheckOutDays;
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
