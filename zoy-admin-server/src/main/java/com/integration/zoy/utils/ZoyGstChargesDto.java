package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyGstChargesDto {
	@SerializedName("rentId")
	private String rentId;

	@SerializedName("gstPercentage")
	private BigDecimal gstPercentage;
	
	@SerializedName("monthlyRent")
	private BigDecimal monthlyRent;

	public String getRentId() {
		return rentId;
	}

	public void setRentId(String rentId) {
		this.rentId = rentId;
	}

	public BigDecimal getGstPercentage() {
		return gstPercentage;
	}

	public void setGstPercentage(BigDecimal gstPercentage) {
		this.gstPercentage = gstPercentage;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
	
	
	
	
}
