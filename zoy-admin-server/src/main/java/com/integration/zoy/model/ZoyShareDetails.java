package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyShareDetails {

	@JsonProperty("zoy_share_id")
	String zoyShareId;

	@JsonProperty("property_share")
	double propertyShare;

	@JsonProperty("zoy_share")
	double zoyShare;

	public String getZoyShareId() {
		return zoyShareId;
	}

	public void setZoyShareId(String zoyShareId) {
		this.zoyShareId = zoyShareId;
	}

	public double getPropertyShare() {
		return propertyShare;
	}

	public void setPropertyShare(double propertyShare) {
		this.propertyShare = propertyShare;
	}

	public double getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(double zoyShare) {
		this.zoyShare = zoyShare;
	}

	

}
