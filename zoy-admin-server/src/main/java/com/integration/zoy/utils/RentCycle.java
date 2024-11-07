package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class RentCycle {
	
	@SerializedName("cycle_id")
	private String cycleId;
	
	@SerializedName("cycle_name")
	private String cycleName;

	public String getCycleId() {
		return cycleId;
	}

	public void setCycleId(String cycleId) {
		this.cycleId = cycleId;
	}

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}
	
	

}
