package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Amenetie {

	@JsonProperty("ameneties")
	private String ameneties;

	@JsonProperty("amenetiesImage")
	private MultipartFile amenetiesImage;

	@JsonProperty("specialAmenity")
	private boolean amenetiesStatus;
	
	public MultipartFile getAmenetiesImage() {
		return amenetiesImage;
	}

	public void setAmenetiesImage(MultipartFile image) {
		this.amenetiesImage = image;
	}

	public String getAmeneties() {
		return ameneties;
	}

	public void setAmeneties(String ameneties) {
		this.ameneties = ameneties;
	}

	public boolean getAmenetiesStatus() {
		return amenetiesStatus;
	}

	public void setAmenetiesStatus(boolean amenetiesStatus) {
		this.amenetiesStatus = amenetiesStatus;
	}

}
