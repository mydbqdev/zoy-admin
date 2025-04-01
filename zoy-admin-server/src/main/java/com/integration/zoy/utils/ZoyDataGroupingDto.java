package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyDataGroupingDto {
	@SerializedName("dataGroupingId")
	private String dataGroupingId;

	@SerializedName("considerDays")
	private int  considerDays;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;
	
	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

	public String getDataGroupingId() {
		return dataGroupingId;
	}

	public void setDataGroupingId(String dataGroupingId) {
		this.dataGroupingId = dataGroupingId;
	}

	public int getConsiderDays() {
		return considerDays;
	}

	public void setConsiderDays(int considerDays) {
		this.considerDays = considerDays;
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
