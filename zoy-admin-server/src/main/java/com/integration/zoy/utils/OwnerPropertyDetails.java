package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OwnerPropertyDetails {

	@SerializedName("ownerId")
	String ownerId;

	@SerializedName("ownerName")
	String ownerName;

	@SerializedName("property_details")
	List<PropertyDetails> propertyDetails;


	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}

	public void setPropertyDetails(List<PropertyDetails> propertyDetails) {
		this.propertyDetails = propertyDetails;
	}
	public List<PropertyDetails> getPropertyDetails() {
		return propertyDetails;
	}

}
