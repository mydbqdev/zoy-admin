package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyTypeId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("currencyName")
    private String currencyName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	

}
