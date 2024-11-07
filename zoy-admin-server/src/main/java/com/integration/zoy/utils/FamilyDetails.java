package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class FamilyDetails {
	@SerializedName("father_name")
	String fatherName;

	@SerializedName("father_num")
	String fatherNum;


	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getFatherName() {
		return fatherName;
	}

	public void setFatherNum(String fatherNum) {
		this.fatherNum = fatherNum;
	}
	public String getFatherNum() {
		return fatherNum;
	}
}
