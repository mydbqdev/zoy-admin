package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ProfileImage {

	@SerializedName("imageUrl")
	String imageUrl;


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}

}
