package com.integration.zoy.model;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class CancellationDetails {
	@SerializedName("bookingId")
	private String bookingId;
	
	@SerializedName("propertyName")
	private String propertyName;
	
	@SerializedName("bedNumber")
	private String bedNumber;
	
	@SerializedName("bookingDate")
    private Timestamp bookingDate;
	
	@SerializedName("cancellationDate")
    private Timestamp cancellationDate;
	
	@SerializedName("monthlyRent")
    private String monthlyRent;
	
	@SerializedName("securityDeposit")
    private String securityDeposit;
	
	@SerializedName("propertyAddress")
    private String propertyAddress; 
	
	@SerializedName("propertyContactNumber")
    private String propertyContactNumber;
	
	@SerializedName("bookingStatus")
    private String bookingStatus;

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public Timestamp getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Timestamp bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Timestamp getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Timestamp cancellationDate) {
		this.cancellationDate = cancellationDate;
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

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
   
    
    
}
