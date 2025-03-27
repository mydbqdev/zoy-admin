package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyShortTermDto {
	@SerializedName("shortTermId")
	private String shortTermId;

	@SerializedName("percentage")
	private BigDecimal percentage;

	@SerializedName("percentage")
	private int startDay;

	@SerializedName("percentage")
	private int endDay;

	public String getShortTermId() {
		return shortTermId;
	}

	public void setShortTermId(String shortTermId) {
		this.shortTermId = shortTermId;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public int getStartDay() {
		return startDay;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

}
