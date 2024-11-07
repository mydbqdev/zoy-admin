package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillingTypeId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("billingTypeName")
    private String billingTypeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillingTypeName() {
		return billingTypeName;
	}

	public void setBillingTypeName(String billingTypeName) {
		this.billingTypeName = billingTypeName;
	}

	

	


}
