package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EkycTypeId {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("userEkycTypeName")
	private String userEkycTypeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserEkycTypeName() {
		return userEkycTypeName;
	}

	public void setUserEkycTypeName(String userEkycTypeName) {
		this.userEkycTypeName = userEkycTypeName;
	}



}
