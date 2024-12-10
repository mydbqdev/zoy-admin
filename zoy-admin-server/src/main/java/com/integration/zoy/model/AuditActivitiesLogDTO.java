package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuditActivitiesLogDTO {
	
	@JsonProperty("created_on")
	private String createdOn;
	
	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("type")
    private String type;
	
	@JsonProperty("history_data")
    private String historyData;
	
   	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getHistoryData() {
		return historyData;
	}

	public void setHistoryData(String historyData) {
		this.historyData = historyData;
	}

}
