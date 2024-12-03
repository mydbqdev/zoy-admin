package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyDataGrouping {

	@JsonProperty("data_grouping_id")
	String dataGroupingId;

	@JsonProperty("data_grouping_name")
	String dataGroupingName;

	public String getDataGroupingId() {
		return dataGroupingId;
	}

	public void setDataGroupingId(String dataGroupingId) {
		this.dataGroupingId = dataGroupingId;
	}

	public String getDataGroupingName() {
		return dataGroupingName;
	}

	public void setDataGroupingName(String dataGroupingName) {
		this.dataGroupingName = dataGroupingName;
	}

	
	

}
