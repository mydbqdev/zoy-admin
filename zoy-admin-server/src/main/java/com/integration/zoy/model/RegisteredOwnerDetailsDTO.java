package com.integration.zoy.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisteredOwnerDetailsDTO {

	@JsonProperty("tickeNumber")
	private String tickeNumber;
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("ownerEmail")
	private String ownerEmail;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("propertyName")
	private String propertyName;

	@JsonProperty("address")
	private String address;

	@JsonProperty("pincode")
	private String pincode;

	@JsonProperty("inquiredFor")
	private String inquiredFor;

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("status")
	private String status;

	@JsonProperty("state")
	private String state;

	@JsonProperty("city")
	private String city;

	@JsonProperty("assignedTo")
	private String assignedTo;

	@JsonProperty("assignedToName")
	private String assignedToName;

	@JsonProperty("description")
	private String description;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("userTicketHistoryDTO")
	private List<UserTicketHistoryDTO> userTicketHistory;

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getInquiredFor() {
		return inquiredFor;
	}

	public void setInquiredFor(String inquiredFor) {
		this.inquiredFor = inquiredFor;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public String getTickeNumber() {
		return tickeNumber;
	}

	public void setTickeNumber(String tickeNumber) {
		this.tickeNumber = tickeNumber;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public List<UserTicketHistoryDTO> getUserTicketHistory() {
		return userTicketHistory;
	}

	public void setUserTicketHistory(List<UserTicketHistoryDTO> userTicketHistory) {
		this.userTicketHistory = userTicketHistory;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
