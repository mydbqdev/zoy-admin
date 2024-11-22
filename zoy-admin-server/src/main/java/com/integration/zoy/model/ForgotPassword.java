package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForgotPassword {
	 @JsonProperty("email")
	 private String email = null;
	 
	 public ForgotPassword email(String email) {
		    this.email = email;
		    return this;
		  }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	 
	 
}
