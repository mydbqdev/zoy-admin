package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueType {

	@JsonProperty("dueTypeName")
	private String dueTypeName;

	public String getDueTypeName() {
		return dueTypeName;
	}

	public void setDueTypeName(String dueTypeName) {
		this.dueTypeName = dueTypeName;
	}


}
