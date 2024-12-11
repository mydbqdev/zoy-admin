package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyPgSecurityDepositRefundRuleDto {
	@SerializedName("ruleId")
	private String ruleId;

	@SerializedName("maximumDays")
	private Integer maximumDays;
	
	@SerializedName("plotformCharges")
	private BigDecimal plotformCharges;
	
	

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public Integer getMaximumDays() {
		return maximumDays;
	}

	public void setMaximumDays(Integer maximumDays) {
		this.maximumDays = maximumDays;
	}

	public BigDecimal getPlotformCharges() {
		return plotformCharges;
	}

	public void setPlotformCharges(BigDecimal plotformCharges) {
		this.plotformCharges = plotformCharges;
	} 



}
