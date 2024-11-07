package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmenetiesId {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("ameneties")
	private String ameneties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAmeneties() {
		return ameneties;
	}

	public void setAmeneties(String ameneties) {
		this.ameneties = ameneties;
	}

	@Override
	public String toString() {
		return "Ameneties [id=" + id + ", ameneties=" + ameneties + "]";
	}

}
