package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortTerm {

	@JsonProperty("zoy_pg_short_term_master_id")
	private String zoyPgShortTermMasterId;

	@JsonProperty("start_day")
	private int startDay;

	@JsonProperty("end_day")
	private int endDay;

	@JsonProperty("percentage")
	private BigDecimal percentage;

	public String getZoyPgShortTermMasterId() {
		return zoyPgShortTermMasterId;
	}

	public void setZoyPgShortTermMasterId(String zoyPgShortTermMasterId) {
		this.zoyPgShortTermMasterId = zoyPgShortTermMasterId;
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

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}


}
