package com.integration.zoy.model;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyPgAutoCancellationPeriodDto {
	@SerializedName("autoCancellationId")
	private String autoCancellationId;

	@SerializedName("daysToCancel")
	private Integer daysToCancel;
	
	@SerializedName("deductionPercentage")
	private BigDecimal deductionPercentage;

	public String getAutoCancellationId() {
		return autoCancellationId;
	}

	public void setAutoCancellationId(String autoCancellationId) {
		this.autoCancellationId = autoCancellationId;
	}

	public Integer getDaysToCancel() {
		return daysToCancel;
	}

	public void setDaysToCancel(Integer daysToCancel) {
		this.daysToCancel = daysToCancel;
	}

	public BigDecimal getDeductionPercentage() {
		return deductionPercentage;
	}

	public void setDeductionPercentage(BigDecimal deductionPercentage) {
		this.deductionPercentage = deductionPercentage;
	}
	
	
}
