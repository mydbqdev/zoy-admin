package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class ZoyPgEarlyCheckOutRuleDto {

	@SerializedName("early_check_out_id")
	private String earlyCheckOutId;

	@SerializedName("trigger_on")
	private String triggerOn;

	@SerializedName("trigger_condition")
	private String triggerCondition;

	@SerializedName("check_out_day")
	private Long checkOutDay;

	@SerializedName("deduction_percentage")
	private BigDecimal deductionPercentage;

	@SerializedName("cond")
	private String cond;

	@SerializedName("trigger_value")
	private String triggerValue;

	@SerializedName("effectiveDate")
	private String effectiveDate;

	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

	@SerializedName("comments")
    private String comments;
	 
	public String getEarlyCheckOutId() {
		return earlyCheckOutId;
	}

	public void setEarlyCheckOutId(String earlyCheckOutId) {
		this.earlyCheckOutId = earlyCheckOutId;
	}

	public String getTriggerOn() {
		return triggerOn;
	}

	public void setTriggerOn(String triggerOn) {
		this.triggerOn = triggerOn;
	}

	public String getTriggerCondition() {
		return triggerCondition;
	}

	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}

	public Long getCheckOutDay() {
		return checkOutDay;
	}

	public void setCheckOutDay(Long checkOutDay) {
		this.checkOutDay = checkOutDay;
	}

	public BigDecimal getDeductionPercentage() {
		return deductionPercentage;
	}

	public void setDeductionPercentage(BigDecimal deductionPercentage) {
		this.deductionPercentage = deductionPercentage;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public String getTriggerValue() {
		return triggerValue;
	}

	public void setTriggerValue(String triggerValue) {
		this.triggerValue = triggerValue;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
