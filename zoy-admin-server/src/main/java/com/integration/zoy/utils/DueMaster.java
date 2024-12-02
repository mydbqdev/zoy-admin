package com.integration.zoy.utils;

import javax.persistence.Column;

import com.google.gson.annotations.SerializedName;

public class DueMaster {
	@SerializedName("due_type_id")
	private String dueTypeId;

	@SerializedName("due_type_name")
	private String dueTypeName;

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
	
	
	

}
