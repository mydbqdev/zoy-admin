package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class RegisterLeadDetails {
	
	@SerializedName("inquiryNumber")
	private String  inquiryNumber;
	
	@SerializedName("name")
	private String  name;
	
	@SerializedName("inquiredFor")
	private String  inquiredFor;
	
	@SerializedName("registeredDate")
	private Timestamp  registeredDate;
	
	@SerializedName("asignedTo")
	private String  asignedTo;
	
	@SerializedName("status")
	private String  status;
	
	@SerializedName("phoneNumber")
	private String phoneNumber;
	
	@SerializedName("propertyName")
	private String propertyName;
	
	@SerializedName("state")
	private String state;
	
	@SerializedName("city")
	private String city;
	
	@SerializedName("email")
	private String ownerEmail;
	
	@SerializedName("description")
	private String description;

	public String getInquiryNumber() {
		return inquiryNumber;
	}

	public void setInquiryNumber(String inquiryNumber) {
		this.inquiryNumber = inquiryNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInquiredFor() {
		return inquiredFor;
	}

	public void setInquiredFor(String inquiredFor) {
		this.inquiredFor = inquiredFor;
	}

	public Timestamp getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}

	public String getAsignedTo() {
		return asignedTo;
	}

	public void setAsignedTo(String asignedTo) {
		this.asignedTo = asignedTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
