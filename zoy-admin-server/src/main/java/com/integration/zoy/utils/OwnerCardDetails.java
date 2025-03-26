package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class OwnerCardDetails {
	@SerializedName("registerOwnerCount")
    private Long registerOwnerCount;

	public Long getRegisterOwnerCount() {
		return registerOwnerCount;
	}

	public void setRegisterOwnerCount(Long registerOwnerCount) {
		this.registerOwnerCount = registerOwnerCount;
	}

	public OwnerCardDetails(Long registerOwnerCount) {
		super();
		this.registerOwnerCount = registerOwnerCount;
	}
	
	
}
