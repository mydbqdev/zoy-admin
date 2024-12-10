package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyOtherChargesDto {

	@SerializedName("otherChargesId")
	private String otherChargesId;

	@SerializedName("otherGst")
	private BigDecimal otherGst;

	@SerializedName("documentCharges")
	private BigDecimal documentCharges;

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
