package com.integration.zoy.utils;


import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyPgTokenDetailsDTO {

	@SerializedName("tokenId")
	private String tokenId;
	
	@SerializedName("fixedToken")
	private BigDecimal fixedToken;
	
	@SerializedName("variableToken")
	private BigDecimal variableToken;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public BigDecimal getFixedToken() {
		return fixedToken;
	}

	public void setFixedToken(BigDecimal fixedToken) {
		this.fixedToken = fixedToken;
	}

	public BigDecimal getVariableToken() {
		return variableToken;
	}

	public void setVariableToken(BigDecimal variableToken) {
		this.variableToken = variableToken;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

}
