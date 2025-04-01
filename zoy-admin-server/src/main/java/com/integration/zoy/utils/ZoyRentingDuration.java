package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyRentingDuration {
	
	@SerializedName("rentingDurationId")
	private String rentingDurationId;

	@SerializedName("rentingDurationDays")
	private int  rentingDurationDays;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;
	
	public String getRentingDurationId() {
		return rentingDurationId;
	}

	public void setRentingDurationId(String rentingDurationId) {
		this.rentingDurationId = rentingDurationId;
	}

	public int getRentingDurationDays() {
		return rentingDurationDays;
	}

	public void setRentingDurationDays(int rentingDurationDays) {
		this.rentingDurationDays = rentingDurationDays;
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
