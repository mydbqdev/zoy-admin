package com.integration.zoy.model;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
