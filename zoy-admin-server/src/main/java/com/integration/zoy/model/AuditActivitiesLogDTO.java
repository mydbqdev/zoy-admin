package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuditActivitiesLogDTO {
	
	@JsonProperty("created_on")
	private String createdOn;
	
	@JsonProperty("history_data")
    private String historyData;
	
    @JsonProperty("search_text")
    private String searchText;

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
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
