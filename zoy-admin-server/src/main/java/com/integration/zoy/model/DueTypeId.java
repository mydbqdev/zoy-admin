package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DueTypeId {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("dueTypeName")
	private String dueTypeName;

	@JsonProperty("dueTypeImage")
	private MultipartFile dueTypeImage;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
