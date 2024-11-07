package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class AmenitiesResponse {
	@SerializedName("ameneties")
	Ameneties ameneties;


	public void setAmeneties(Ameneties ameneties) {
		this.ameneties = ameneties;
	}
	public Ameneties getAmeneties() {
		return ameneties;
	}
}
