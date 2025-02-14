package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueType {

	@JsonProperty("dueTypeName")
	private String dueTypeName;

	@JsonProperty("dueTypeImage")
	private MultipartFile dueTypeImage;
	
	public String getDueTypeName() {
		return dueTypeName;
	}

	public void setDueTypeName(String dueTypeName) {
		this.dueTypeName = dueTypeName;
	}

	public MultipartFile getDueTypeImage() {
		return dueTypeImage;
	}

	public void setDueTypeImage(MultipartFile dueTypeImage) {
		this.dueTypeImage = dueTypeImage;
	}

	

}
