package com.integration.zoy.entity;


import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "zoy_pg_auto_cancellation_master", schema = "pgowners")
public class ZoyPgAutoCancellationMaster {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "auto_cancellation_id", updatable = false, nullable = false, unique = true, length = 36)
	private String autoCancellationId;

	@Column(name = "trigger_on", length = 100)
	private String triggerOn;

	@Column(name = "trigger_condition", length = 10)
	private String triggerCondition;

	@Column(name = "auto_cancellation_day", nullable = false)
	private Long autoCancellationDay;

	@Column(name = "deduction_percentage", precision = 10, scale = 2)
	private BigDecimal deductionPercentage;

	@Column(name = "cond", length = 100)
	private String cond;

	@Column(name = "trigger_value", length = 100)
	private String triggerValue;

	// Getters and Setters

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

	@Override
	public String toString() {
		return "ZoyPgAutoCancellationMaster [autoCancellationId=" + autoCancellationId + ", triggerOn=" + triggerOn
				+ ", triggerCondition=" + triggerCondition + ", autoCancellationDay=" + autoCancellationDay
				+ ", deductionPercentage=" + deductionPercentage + ", cond=" + cond + ", triggerValue=" + triggerValue
				+ "]";
	}
	
}