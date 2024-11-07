package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Amenetie {

	@JsonProperty("ameneties")
	private String ameneties;

	public String getAmeneties() {
		return ameneties;
	}

	public void setAmeneties(String ameneties) {
		this.ameneties = ameneties;
	}
	
	
}
