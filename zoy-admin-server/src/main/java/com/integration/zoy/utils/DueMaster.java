package com.integration.zoy.utils;

import org.springframework.web.multipart.MultipartFile;

import com.google.gson.annotations.SerializedName;


public class DueMaster {
	@SerializedName("due_type_id")
	private String dueTypeId;

	@SerializedName("due_type_name")
	private String dueTypeName;

	@SerializedName("due_type_image")
	private MultipartFile dueTypeImage;

	public String getDueTypeId() {
		return dueTypeId;
	}

	public void setDueTypeId(String dueTypeId) {
		this.dueTypeId = dueTypeId;
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
