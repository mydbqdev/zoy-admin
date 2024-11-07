package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;


public class PropertyInformation {

	@SerializedName("gstNumber")
	String gstNumber;

	@SerializedName("pgType")
	String pgType;

	@SerializedName("pgType_Id")
	String pgTypeId;

	@SerializedName("cinNumber")
	String cinNumber;

	@SerializedName("gstStatus")
	Boolean gstStatus;

	@SerializedName("gstPercent")
	String gstPercent;


	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}
	public String getGstNumber() {
		return gstNumber;
	}

	public void setPgType(String pgType) {
		this.pgType = pgType;
	}
	public String getPgType() {
		return pgType;
	}

	public void setCinNumber(String cinNumber) {
		this.cinNumber = cinNumber;
	}
	public String getCinNumber() {
		return cinNumber;
	}
	public String getPgTypeId() {
		return pgTypeId;
	}
	public void setPgTypeId(String pgTypeId) {
		this.pgTypeId = pgTypeId;
	}
	public Boolean getGstStatus() {
		return gstStatus;
	}
	public void setGstStatus(Boolean gstStatus) {
		this.gstStatus = gstStatus;
	}
	public String getGstPercent() {
		return gstPercent;
	}
	public void setGstPercent(String gstPercent) {
		this.gstPercent = gstPercent;
	}



}