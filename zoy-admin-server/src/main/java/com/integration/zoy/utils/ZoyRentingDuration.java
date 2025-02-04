package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyRentingDuration {
	@SerializedName("rentingDurationId")
	private String rentingDurationId;

	@SerializedName("rentingDurationDays")
	private int  rentingDurationDays;

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
	
	

}
