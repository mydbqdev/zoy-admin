package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class Beds {

	@SerializedName("bed_id")
	String bedId;

	@SerializedName("bed_name")
	String bedName;

	public Beds(String bedId, String bedName) {
		this.bedId=bedId;
		this.bedName=bedName;
	}

	public String getBedId() {
		return bedId;
	}

	public void setBedId(String bedId) {
		this.bedId = bedId;
	}

	public String getBedName() {
		return bedName;
	}

	public void setBedName(String bedName) {
		this.bedName = bedName;
	}
	

}
