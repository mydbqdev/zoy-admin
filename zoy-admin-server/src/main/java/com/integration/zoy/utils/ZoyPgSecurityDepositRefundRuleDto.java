package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyPgSecurityDepositRefundRuleDto {
	@SerializedName("ruleId")
	private String ruleId;

	@SerializedName("maximumDays")
	private Integer maximumDays;

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



}
