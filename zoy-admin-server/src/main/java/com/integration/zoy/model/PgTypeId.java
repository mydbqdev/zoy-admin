package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgTypeId {
	@JsonProperty("id")
	String id;

	@JsonProperty("pgTypeName")
	String pgTypeName;

	public String getPgTypeName() {
		return pgTypeName;
	}

	public void setPgTypeName(String pgTypeName) {
		this.pgTypeName = pgTypeName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
