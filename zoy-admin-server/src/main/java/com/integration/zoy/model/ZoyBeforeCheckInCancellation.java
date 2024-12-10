package com.integration.zoy.model;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyBeforeCheckInCancellation {

	@SerializedName("cancellationId")
	String cancellationId;

	@SerializedName("daysBeforeCheckIn")
	int daysBeforeCheckIn;
	
	@SerializedName("deductionPercentages")
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
