package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgBlacklistedModel {


	@JsonProperty("pg_blacklisted_id")
    private String id;

	@JsonProperty("pg_blocklisted_email")
    private String email;

	@JsonProperty("pg_blacklisted_mobile")
    private String mobile;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	

}
