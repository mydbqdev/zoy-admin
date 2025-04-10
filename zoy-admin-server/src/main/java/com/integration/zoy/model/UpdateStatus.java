package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateStatus {

	@JsonProperty("inquiryNumber")
	private String inquiryNumber;
	
	@JsonProperty("status")
	private String status;

	public String getInquiryNumber() {
		return inquiryNumber;
	}

	public void setInquiryNumber(String inquiryNumber) {
		this.inquiryNumber = inquiryNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
