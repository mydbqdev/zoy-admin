package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
