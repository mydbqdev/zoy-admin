package com.integration.zoy.model;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class UpcomingBookingDetails {
	@SerializedName("bookingId")
    private String bookingId;

    @SerializedName("bookingDate")
    private Timestamp bookingDate;

    @SerializedName("propertyName")
    private String propertyName;

    @SerializedName("propertyAddress")
    private String propertyAddress; 

    @SerializedName("propertyContactNumber")
    private String propertyContactNumber;

    @SerializedName("bedNumber")
    private String bedNumber;

    @SerializedName("monthlyRent")
    private String monthlyRent;

    @SerializedName("securityDeposit")
    private String securityDeposit;

    @SerializedName("securityDepositStatus")
    private String securityDepositStatus;

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public Timestamp getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Timestamp bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getPropertyContactNumber() {
		return propertyContactNumber;
	}

	public void setPropertyContactNumber(String propertyContactNumber) {
		this.propertyContactNumber = propertyContactNumber;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public String getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(String monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public String getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(String securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public String getSecurityDepositStatus() {
		return securityDepositStatus;
	}

	public void setSecurityDepositStatus(String securityDepositStatus) {
		this.securityDepositStatus = securityDepositStatus;
	}
    
    
}
