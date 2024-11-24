package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OwnerPropertyDTO {
	
	@JsonProperty("owner_id")
	private String owner_id;
	
	@JsonProperty("owner_name")
    private String owner_name;
	
	@JsonProperty("owner_email")
    private String owner_email;
	
	@JsonProperty("owner_contact")
    private String owner_contact;
	
	@JsonProperty("number_of_properties")
    private long number_of_properties;
	
	@JsonProperty("status")
	private String status;

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getOwner_email() {
		return owner_email;
	}

	public void setOwner_email(String owner_email) {
		this.owner_email = owner_email;
	}

	public String getOwner_contact() {
		return owner_contact;
	}

	public void setOwner_contact(String owner_contact) {
		this.owner_contact = owner_contact;
	}

	public long getNumber_of_properties() {
		return number_of_properties;
	}

	public void setNumber_of_properties(long number_of_properties) {
		this.number_of_properties = number_of_properties;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OwnerPropertyDTO(String owner_id, String owner_name, String owner_email, String owner_contact,
			long number_of_properties, String status) {
		super();
		this.owner_id = owner_id;
		this.owner_name = owner_name;
		this.owner_email = owner_email;
		this.owner_contact = owner_contact;
		this.number_of_properties = number_of_properties;
		this.status = status;
	}
	

	
	

	
	
}
