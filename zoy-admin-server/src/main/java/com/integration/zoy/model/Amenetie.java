package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Amenetie {

	@JsonProperty("ameneties")
	private String ameneties;

	@JsonProperty("image")
	private MultipartFile image;

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public String getAmeneties() {
		return ameneties;
	}

	public void setAmeneties(String ameneties) {
		this.ameneties = ameneties;
	}

}
