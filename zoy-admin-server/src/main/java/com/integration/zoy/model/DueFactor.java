package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueFactor {
	@JsonProperty("factorName")
	private String factorName;

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
	}


}
