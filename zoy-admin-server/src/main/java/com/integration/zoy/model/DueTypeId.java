package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueTypeId {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("dueTypeName")
	private String dueTypeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDueTypeName() {
		return dueTypeName;
	}

	public void setDueTypeName(String dueTypeName) {
		this.dueTypeName = dueTypeName;
	}


}
