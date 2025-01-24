package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyShortTermDto {
	@SerializedName("shortTermId")
	private String shortTermId;

	@SerializedName("percentage")
	private BigDecimal percentage;
	
	@SerializedName("days")
	private String days;

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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	
	
	
	
}
