package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class AddDueResponse {

	@SerializedName("message")
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
