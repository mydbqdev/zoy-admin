package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyBeforeCheckInCancellation {

	@JsonProperty("cancellation_id")
	String cancellationId;

	@JsonProperty("before_checkin_days")
	int daysBeforeCheckIn;
	
	@JsonProperty("deduction_percentage")
	BigDecimal deductionPercentages;


	public int getDaysBeforeCheckIn() {
		return daysBeforeCheckIn;
	}

	public void setDaysBeforeCheckIn(int daysBeforeCheckIn) {
		this.daysBeforeCheckIn = daysBeforeCheckIn;
	}

	public BigDecimal getDeductionPercentages() {
		return deductionPercentages;
	}

	public void setDeductionPercentages(BigDecimal deductionPercentages) {
		this.deductionPercentages = deductionPercentages;
	}

	

	public String getCancellationId() {
		return cancellationId;
	}

	public void setCancellationId(String cancellationId) {
		this.cancellationId = cancellationId;
	}

	

}
