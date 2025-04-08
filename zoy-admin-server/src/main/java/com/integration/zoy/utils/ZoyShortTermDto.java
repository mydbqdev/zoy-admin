package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyShortTermDto {
	@JsonProperty("shortTermId")
	private String shortTermId;

	@JsonProperty("percentage")
	private BigDecimal percentage;

	@JsonProperty("startDay")
	private int startDay;

	@JsonProperty("endDay")
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
