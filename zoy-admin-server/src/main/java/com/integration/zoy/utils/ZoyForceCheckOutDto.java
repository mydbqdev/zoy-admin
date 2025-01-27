package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyForceCheckOutDto {
	@SerializedName("id")
	private String forceCheckOutId;

	@SerializedName("forceCheckOutDays")
	private int  forceCheckOutDays;

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

	
	


}
