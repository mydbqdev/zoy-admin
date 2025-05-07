package com.integration.zoy.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComplaintTicketDTO {
	
	@JsonProperty("tickeNumber")
	private String tickeNumber;

	@JsonProperty("userHelpRequestId")
	private String userHelpRequestId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("propertyName")
	private String propertyName;

	@JsonProperty("categoriesName")
	private String categoriesName;

	@JsonProperty("description")
	private String description;

	@JsonProperty("urgency")
	private String urgency;

	@JsonProperty("status")
	private String status;

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("updatedAt")
	private String updatedAt;

	@JsonProperty("assignTo")
	private String assignTo;

	@JsonProperty("assignToName")
	private String assignToName;
	
	@JsonProperty("imagesUrls")
	private String imagesUrls;
	
	@JsonProperty("type")
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("userTicketHistoryDTO")
	private List<UserTicketHistoryDTO> userTicketHistory;

	// Getters and Setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getCategoriesName() {
		return categoriesName;
	}

	public void setCategoriesName(String categoriesName) {
		this.categoriesName = categoriesName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	
	public String getAssignToName() {
		return assignToName;
	}

	public void setAssignToName(String assignToName) {
		this.assignToName = assignToName;
	}

	public String getUserHelpRequestId() {
		return userHelpRequestId;
	}

	public void setUserHelpRequestId(String userHelpRequestId) {
		this.userHelpRequestId = userHelpRequestId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	public String getImagesUrls() {
		return imagesUrls;
	}

	public void setImagesUrls(String imagesUrls) {
		this.imagesUrls = imagesUrls;
	}

	public String getTickeNumber() {
		return tickeNumber;
	}

	public void setTickeNumber(String tickeNumber) {
		this.tickeNumber = tickeNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public List<UserTicketHistoryDTO> getUserTicketHistory() {
		return userTicketHistory;
	}

	public void setUserTicketHistory(List<UserTicketHistoryDTO> userTicketHistory) {
		this.userTicketHistory = userTicketHistory;
	}

}
