package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	
}
