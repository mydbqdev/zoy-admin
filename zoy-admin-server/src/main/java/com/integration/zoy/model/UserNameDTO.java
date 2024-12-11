package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNameDTO {
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("useremail")
	private String useremail;

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public UserNameDTO(String userName, String userEmail) {
        this.username = userName;
        this.useremail = userEmail;
    }
	
}
