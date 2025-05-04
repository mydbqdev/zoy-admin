package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateStatus {

	@JsonProperty("inquiryNumber")
	private String inquiryNumber;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("inquiryType")
	private String inquiryType;
	
	@JsonProperty("comment")
	private String comment;

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

	public String getInquiryType() {
		return inquiryType;
	}

	public void setInquiryType(String inquiryType) {
		this.inquiryType = inquiryType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
		
}
