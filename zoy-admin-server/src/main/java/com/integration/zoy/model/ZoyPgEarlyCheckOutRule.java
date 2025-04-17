package com.integration.zoy.model;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class ZoyPgEarlyCheckOutRule {
	@JsonProperty("early_check_out_id")
    private String earlyCheckOutId;

    @JsonProperty("trigger_on")
    private String triggerOn;

    @JsonProperty("trigger_condition")
    private String triggerCondition;

    @JsonProperty("check_out_day")
    private Long checkOutDay;

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

    @JsonProperty("comments")
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

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
    
}
