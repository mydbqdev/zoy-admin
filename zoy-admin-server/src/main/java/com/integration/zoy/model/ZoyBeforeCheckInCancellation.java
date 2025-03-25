package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyBeforeCheckInCancellation {

	@JsonProperty("cancellation_id")
	private String cancellationId;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("trigger_on")
	private String triggerOn;

	@JsonProperty("trigger_condition")
	private String triggerCondition;

	@JsonProperty("before_checkin_days")
	private Integer beforeCheckinDays;

	@JsonProperty("deduction_percentage")
	private BigDecimal deductionPercentage;

	@JsonProperty("cond")
	private String cond;

	@JsonProperty("trigger_value")
	private String triggerValue;

	@JsonProperty("create_at")
	private Timestamp createAt;
	
	@JsonProperty("isDelete")
	private Boolean isDelete;
	
	@JsonProperty("isEdit")
	private Boolean isEdit;
	


	public String getCancellationId() {
		return cancellationId;
	}

	public void setCancellationId(String cancellationId) {
		this.cancellationId = cancellationId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public Integer getBeforeCheckinDays() {
		return beforeCheckinDays;
	}

	public void setBeforeCheckinDays(Integer beforeCheckinDays) {
		this.beforeCheckinDays = beforeCheckinDays;
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

	public Timestamp getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}
	
}
