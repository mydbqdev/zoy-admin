package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

// Nested Review class
public class Review {

	@SerializedName("profile")
	private String profile;

	@SerializedName("name")
	private String name;

	@SerializedName("dateTime")
	private Timestamp dateTime;

	@SerializedName("review")
	private String review;

	// Getters and Setters

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}
}
