package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketAssign {

	@JsonProperty("email")
	private String email;
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("inquiryNumber")
	private String inquiryNumber;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInquiryNumber() {
		return inquiryNumber;
	}

	public void setInquiryNumber(String inquiryNumber) {
		this.inquiryNumber = inquiryNumber;
	}
	
	
	
}
