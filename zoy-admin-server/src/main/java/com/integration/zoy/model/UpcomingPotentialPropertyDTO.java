package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class UpcomingPotentialPropertyDTO {

	@SerializedName("ownerFullName")
	private String ownerFullName;

	@SerializedName("propertyName")
	private String propertyName;

	@SerializedName("ownerEmailAddress")
	private String ownerEmailAddress;

	@SerializedName("ownerContactNumber")
	private String ownerContactNumber;

	@SerializedName("propertyAddress")
	private String propertyAddress;

	// Getters and Setters

	public String getOwnerFullName() {
		return ownerFullName;
	}

	public void setOwnerFullName(String ownerFullName) {
		this.ownerFullName = ownerFullName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getOwnerEmailAddress() {
		return ownerEmailAddress;
	}

	public void setOwnerEmailAddress(String ownerEmailAddress) {
		this.ownerEmailAddress = ownerEmailAddress;
	}

	public String getOwnerContactNumber() {
		return ownerContactNumber;
	}

	public void setOwnerContactNumber(String ownerContactNumber) {
		this.ownerContactNumber = ownerContactNumber;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}
}
