package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FloorName {
	@JsonProperty("floorName")
	private String floorName;

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	
}
