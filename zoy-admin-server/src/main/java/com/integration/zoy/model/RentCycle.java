package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RentCycle {
	@JsonProperty("rentCycleName")
	private String rentCycleName;

	public String getRentCycleName() {
		return rentCycleName;
	}

	public void setRentCycleName(String rentCycleName) {
		this.rentCycleName = rentCycleName;
	}


}
