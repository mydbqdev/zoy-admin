package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyType {
	@JsonProperty("currencyName")
    private String currencyName;

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	

}
