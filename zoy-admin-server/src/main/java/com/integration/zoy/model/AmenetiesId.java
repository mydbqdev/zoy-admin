package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmenetiesId {

	@JsonProperty("id")
	private String id;

	@JsonProperty("ameneties")
	private String ameneties;

	@JsonProperty("amenetiesImage")
	private MultipartFile amenetiesImage;

	@JsonProperty("specialAmenity")
	private boolean amenetiesStatus;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAmeneties() {
		return ameneties;
	}

	public void setAmeneties(String ameneties) {
		this.ameneties = ameneties;
	}

	public MultipartFile getAmenetiesImage() {
		return amenetiesImage;
	}

	public void setAmenetiesImage(MultipartFile amenetiesImage) {
		this.amenetiesImage = amenetiesImage;
	}

	@Override
	public String toString() {
		return "Ameneties [id=" + id + ", ameneties=" + ameneties + "]";
	}

	public boolean getAmenetiesStatus() {
		return amenetiesStatus;
	}

	public void setAmenetiesStatus(boolean amenetiesStatus) {
		this.amenetiesStatus = amenetiesStatus;
	}

}
