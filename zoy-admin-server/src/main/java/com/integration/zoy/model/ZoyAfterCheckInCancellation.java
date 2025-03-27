package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyAfterCheckInCancellation {

	@JsonProperty("auto_cancellation_id")
	private String autoCancellationId;

	@JsonProperty("trigger_on")
	private String triggerOn;

	@JsonProperty("trigger_condition")
	private String triggerCondition;

	@JsonProperty("auto_cancellation_day")
	private Long autoCancellationDay;

	@JsonProperty("deduction_percentage")
	private BigDecimal deductionPercentage;

	@JsonProperty("cond")
	private String cond;

	@JsonProperty("trigger_value")
	private String triggerValue;

	@JsonProperty("effectiveDate")
	private String effectiveDate;

	@JsonProperty("isApproved")
	private boolean isApproved;

	@JsonProperty("approvedBy")
	private String approvedBy;

	@JsonProperty("createdBy")
	private String createdBy;

	public String getAutoCancellationId() {
		return autoCancellationId;
	}

	public void setAutoCancellationId(String autoCancellationId) {
		this.autoCancellationId = autoCancellationId;
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

	public Long getAutoCancellationDay() {
		return autoCancellationDay;
	}

	public void setAutoCancellationDay(Long autoCancellationDay) {
		this.autoCancellationDay = autoCancellationDay;
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

}
