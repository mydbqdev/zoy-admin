package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyDataGroupingDto {
	@SerializedName("id")
	private String dataGroupingId;

	@SerializedName("considerDays")
	private int  considerDays;

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



}
