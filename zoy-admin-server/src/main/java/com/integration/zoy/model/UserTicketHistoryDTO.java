package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTicketHistoryDTO {

	@JsonProperty("userHelpRequestId")
	private String userHelpRequestId;

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("userEmail")
	private String userEmail;

	@JsonProperty("description")
	private String description;

	@JsonProperty("requestStatus")
	private String requestStatus;

	// Getters and Setters
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

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
}
