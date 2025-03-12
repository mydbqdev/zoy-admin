package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyForceCheckOutDto {
	@SerializedName("id")
	private String forceCheckOutId;

	@SerializedName("forceCheckOutDays")
	private int  forceCheckOutDays;

	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;
	
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

	
	


}
