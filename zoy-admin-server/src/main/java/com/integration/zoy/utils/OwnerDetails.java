package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class OwnerDetails {
	
	
	@SerializedName("mobile")
	private String mobile;
	
	@SerializedName("zoyCode")
	private String zoyCode;
	
	@SerializedName("propertyOwnerName")
	private String propertyOwnerName;
	
	@SerializedName("email")
	private String email;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getZoyCode() {
		return zoyCode;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}

	public String getPropertyOwnerName() {
		return propertyOwnerName;
	}

	public void setPropertyOwnerName(String propertyOwnerName) {
		this.propertyOwnerName = propertyOwnerName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "OwnerDetails [mobile=" + mobile + ", zoyCode=" + zoyCode + ", propertyOwnerName=" + propertyOwnerName
				+ ", email=" + email + "]";
	}

	
	
}
