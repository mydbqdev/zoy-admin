package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyToken {

	@JsonProperty("token_id")
	String tokenId;

	@JsonProperty("fixed_token")
	double fixedToken;

	@JsonProperty("variable_token")
	double variableToken;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public double getFixedToken() {
		return fixedToken;
	}

	public void setFixedToken(double fixedToken) {
		this.fixedToken = fixedToken;
	}

	public double getVariableToken() {
		return variableToken;
	}

	public void setVariableToken(double variableToken) {
		this.variableToken = variableToken;
	}





}
