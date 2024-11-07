package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillingType {
	
	@JsonProperty("billingTypeName")
    private String billingTypeName;

	public String getBillingTypeName() {
		return billingTypeName;
	}

	public void setBillingTypeName(String billingTypeName) {
		this.billingTypeName = billingTypeName;
	}

	

	


}
