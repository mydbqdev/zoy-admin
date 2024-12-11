package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class CancellationID {
	
	@SerializedName("cancellationID")
	String cancellationID;

	public String getCancellationID() {
		return cancellationID;
	}

	public void setCancellationID(String cancellationID) {
		this.cancellationID = cancellationID;
	}
	
	
}
