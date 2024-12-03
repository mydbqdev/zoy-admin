package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyBeforeCheckInCancellation {

	@JsonProperty("cancellation_id")
	String cancellationId;

	@JsonProperty("cancellation_fixed_charges")
	double cancellationFixedCharges;

	@JsonProperty("cancellation_variable_charges")
	double cancellationVariableCharges;

	@JsonProperty("cancellation_days")
	int cancellationDays;

	public String getCancellationId() {
		return cancellationId;
	}

	public void setCancellationId(String cancellationId) {
		this.cancellationId = cancellationId;
	}

	public double getCancellationFixedCharges() {
		return cancellationFixedCharges;
	}

	public void setCancellationFixedCharges(double cancellationFixedCharges) {
		this.cancellationFixedCharges = cancellationFixedCharges;
	}

	public double getCancellationVariableCharges() {
		return cancellationVariableCharges;
	}

	public void setCancellationVariableCharges(double cancellationVariableCharges) {
		this.cancellationVariableCharges = cancellationVariableCharges;
	}

	public int getCancellationDays() {
		return cancellationDays;
	}

	public void setCancellationDays(int cancellationDays) {
		this.cancellationDays = cancellationDays;
	}

}
