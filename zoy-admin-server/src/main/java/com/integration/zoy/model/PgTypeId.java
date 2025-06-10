package com.integration.zoy.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgTypeId {
	@JsonProperty("id")
	String id;

	@JsonProperty("pgTypeName")
	String pgTypeName;

	@JsonProperty("genderIds")
	private List<String> genderIds;

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

	public List<String> getGenderIds() {
		return genderIds;
	}

	public void setGenderIds(List<String> genderIds) {
		this.genderIds = genderIds;
	}

}
