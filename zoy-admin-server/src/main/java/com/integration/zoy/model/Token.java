package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	@JsonProperty("token")
	String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


}
