package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgType {
	@JsonProperty("pgTypeName")
	String pgTypeName;

	public String getPgTypeName() {
		return pgTypeName;
	}

	public void setPgTypeName(String pgTypeName) {
		this.pgTypeName = pgTypeName;
	}
	
	
	
}
