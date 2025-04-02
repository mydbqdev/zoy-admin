package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class ZoyBeforeCheckInCancellation {

	@SerializedName("cancellation_id")
	private String cancellationId;

	@SerializedName("priority")
	private Integer priority;

	@SerializedName("trigger_on")
	private String triggerOn;

	@SerializedName("trigger_condition")
	private String triggerCondition;

	@SerializedName("before_checkin_days")
	private Integer beforeCheckinDays;

	@SerializedName("deduction_percentage")
	private BigDecimal deductionPercentage;

	@SerializedName("cond")
	private String cond;

	@SerializedName("trigger_value")
	private String triggerValue;

	@SerializedName("create_at")
	private Timestamp createAt;
	
	@SerializedName("isDelete")
	private Boolean isDelete;
	
	@SerializedName("isEdit")
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
