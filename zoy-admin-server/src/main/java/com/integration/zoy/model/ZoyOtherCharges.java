package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyOtherCharges {

	@JsonProperty("other_charges_id")
	String otherChargesId;

	@JsonProperty("other_gst")
	double otherGst;

	@JsonProperty("document_charges")
	double documentCharges;

	public String getOtherChargesId() {
		return otherChargesId;
	}

	public void setOtherChargesId(String otherChargesId) {
		this.otherChargesId = otherChargesId;
	}

	public double getOtherGst() {
		return otherGst;
	}

	public void setOtherGst(double otherGst) {
		this.otherGst = otherGst;
	}

	public double getDocumentCharges() {
		return documentCharges;
	}

	public void setDocumentCharges(double documentCharges) {
		this.documentCharges = documentCharges;
	}

	

}
