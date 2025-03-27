package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ZoySecurityDepositDeadLineDto {
	@SerializedName("auto_cancellation_id")
    private String autoCancellationId;

	@SerializedName("trigger_on")
    private String triggerOn;

    @SerializedName("trigger_condition")
    private String triggerCondition;

    @SerializedName("auto_cancellation_day")
    private Long autoCancellationDay;

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
