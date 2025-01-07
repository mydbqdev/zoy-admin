package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoyBeforeCheckInCancellationDto {

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
}
