package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueFactorId {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("factorName")
	private String factorName;

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
