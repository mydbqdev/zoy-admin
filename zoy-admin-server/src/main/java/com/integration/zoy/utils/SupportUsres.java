package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class SupportUsres {

	@SerializedName("email")
	private String email;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("type")
	private String type;
	

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
	
	public String getType() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
