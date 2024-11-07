package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class UserPin {

	@SerializedName("pin")
	private Integer pin;
	
	@SerializedName("mobile")
	private String mobile;
	 
	@SerializedName("email")
	private String email;
	
	@SerializedName("userId")
	private String userId;

	public Integer getPin() {
		return pin;
	}

	public void setPin(Integer pin) {
		this.pin = pin;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	
}
