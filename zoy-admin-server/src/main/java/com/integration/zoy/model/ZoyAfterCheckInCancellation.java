package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoyAfterCheckInCancellation {

	@JsonProperty("auto_cancellation_id")
    private String autoCancellationId;

	@JsonProperty("trigger_on")
    private String triggerOn;

    @JsonProperty("trigger_condition")
    private String triggerCondition;

    @JsonProperty("auto_cancellation_day")
    private Long autoCancellationDay;

    @JsonProperty("deduction_percentage")
    private BigDecimal deductionPercentage;

    @JsonProperty("cond")
    private String cond;

    @JsonProperty("trigger_value")
    private String triggerValue;
}
