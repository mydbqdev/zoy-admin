package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EkycType {

	@JsonProperty("userEkycTypeName")
	private String userEkycTypeName;

	public String getUserEkycTypeName() {
		return userEkycTypeName;
	}

	public void setUserEkycTypeName(String userEkycTypeName) {
		this.userEkycTypeName = userEkycTypeName;
	}



}
