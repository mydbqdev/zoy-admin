package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FloorNameId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("floorName")
	private String floorName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	
	
}
