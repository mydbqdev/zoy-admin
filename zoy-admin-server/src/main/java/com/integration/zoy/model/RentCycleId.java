package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RentCycleId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("rentCycleName")
	private String rentCycleName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRentCycleName() {
		return rentCycleName;
	}

	public void setRentCycleName(String rentCycleName) {
		this.rentCycleName = rentCycleName;
	}


}
