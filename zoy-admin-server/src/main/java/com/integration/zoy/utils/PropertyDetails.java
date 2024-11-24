package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class PropertyDetails {

	@SerializedName("propertyId")
	String propertyId;

	@SerializedName("propertyname")
	String propertyname;

	public PropertyDetails(String propertyId, String propertyname) {
		super();
		this.propertyId = propertyId;
		this.propertyname = propertyname;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}
	public String getPropertyname() {
		return propertyname;
	}

}