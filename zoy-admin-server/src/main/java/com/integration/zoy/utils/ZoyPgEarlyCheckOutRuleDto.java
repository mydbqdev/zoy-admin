package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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



}
