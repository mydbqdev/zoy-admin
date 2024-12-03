package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class BulkUploadData {
	
	@SerializedName("id")
	Long id;

	@SerializedName("category")
	String category;

	@SerializedName("fileName")
	String fileName;

	@SerializedName("ownerId")
	String ownerId;
	
	@SerializedName("ownerName")
	String ownerName;

	@SerializedName("propertyId")
	String propertyId;

	@SerializedName("propertyName")
	String propertyName;
	
	@SerializedName("status")
	String status;
	
	@SerializedName("createAt")
	Timestamp createAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return "BulkUploadData [id=" + id + ", category=" + category + ", fileName=" + fileName + ", ownerId=" + ownerId
				+ ", ownerName=" + ownerName + ", propertyId=" + propertyId + ", propertyName=" + propertyName
				+ ", status=" + status + ", createAt=" + createAt + "]";
	}
	
}
