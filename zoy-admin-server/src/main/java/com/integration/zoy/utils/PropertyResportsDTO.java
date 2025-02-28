package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class PropertyResportsDTO {
	
	@SerializedName("ownerFullName")
    private String ownerFullName;

    @SerializedName("propertyName")
    private String propertyName;

    @SerializedName("propertyContactNumber")
    private String propertyContactNumber;

    @SerializedName("propertyEmailAddress")
    private String propertyEmailAddress;

    @SerializedName("propertyAddress")
    private String propertyAddress;

    @SerializedName("suspendedDate")
    private Timestamp suspendedDate;

    @SerializedName("reasonForSuspension")
    private String reasonForSuspension;

    @SerializedName("numberOfBeds")
    private String numberOfBeds;

    @SerializedName("expectedRentPerMonth")
    private String expectedRentPerMonth;

    @SerializedName("lastCheckOutDate")
    private Timestamp lastCheckOutDate;

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

	public String getPropertyContactNumber() {
		return propertyContactNumber;
	}

	public void setPropertyContactNumber(String propertyContactNumber) {
		this.propertyContactNumber = propertyContactNumber;
	}

	public String getPropertyEmailAddress() {
		return propertyEmailAddress;
	}

	public void setPropertyEmailAddress(String propertyEmailAddress) {
		this.propertyEmailAddress = propertyEmailAddress;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public Timestamp getSuspendedDate() {
		return suspendedDate;
	}

	public void setSuspendedDate(Timestamp suspendedDate) {
		this.suspendedDate = suspendedDate;
	}

	public String getReasonForSuspension() {
		return reasonForSuspension;
	}

	public void setReasonForSuspension(String reasonForSuspension) {
		this.reasonForSuspension = reasonForSuspension;
	}

	public String getNumberOfBeds() {
		return numberOfBeds;
	}

	public void setNumberOfBeds(String numberOfBeds) {
		this.numberOfBeds = numberOfBeds;
	}

	public String getExpectedRentPerMonth() {
		return expectedRentPerMonth;
	}

	public void setExpectedRentPerMonth(String expectedRentPerMonth) {
		this.expectedRentPerMonth = expectedRentPerMonth;
	}

	public Timestamp getLastCheckOutDate() {
		return lastCheckOutDate;
	}

	public void setLastCheckOutDate(Timestamp lastCheckOutDate) {
		this.lastCheckOutDate = lastCheckOutDate;
	}
    
    
}
