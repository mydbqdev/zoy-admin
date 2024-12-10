package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RentalNeedDetails {
	@JsonProperty("timeStamp")
	@JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	Timestamp timeStamp;

	@JsonProperty("isAgreed")
	boolean isAgreed;


	public void setTimeStamp(Timestamp timestamp2) {
		this.timeStamp = timestamp2;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setIsAgreed(boolean isAgreed) {
		this.isAgreed = isAgreed;
	}
	public boolean getIsAgreed() {
		return isAgreed;
	}
}
