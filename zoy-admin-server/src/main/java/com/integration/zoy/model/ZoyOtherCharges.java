package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyOtherCharges {

	@JsonProperty("otherChargesId")
	String otherChargesId;

	@JsonProperty("otherGst")
	BigDecimal otherGst;

	@JsonProperty("documentCharges")
	BigDecimal documentCharges;

	public String getOtherChargesId() {
		return otherChargesId;
	}

	public void setOtherChargesId(String otherChargesId) {
		this.otherChargesId = otherChargesId;
	}

	public BigDecimal getOtherGst() {
		return otherGst;
	}

	public void setOtherGst(BigDecimal otherGst) {
		this.otherGst = otherGst;
	}

	public BigDecimal getDocumentCharges() {
		return documentCharges;
	}

	public void setDocumentCharges(BigDecimal documentCharges) {
		this.documentCharges = documentCharges;
	}

	

	

}
