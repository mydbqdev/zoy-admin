package com.integration.zoy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadTenant {

	@JsonProperty("ownerId")
	String ownerId;

	@JsonProperty("ownerName")
	String ownerName;
	
	@JsonProperty("propertyId")
	String propertyId;

	@JsonProperty("propertyName")
	String propertyName;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String toString() {
		return "UploadTenant [ownerId=" + ownerId + ", ownerName=" + ownerName + ", propertyId=" + propertyId
				+ ", propertyName=" + propertyName + "]";
	}


}