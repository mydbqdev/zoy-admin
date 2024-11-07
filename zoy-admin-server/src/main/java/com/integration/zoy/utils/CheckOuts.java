package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CheckOuts {

	@SerializedName("checkOuts")
	private List<String> checkOuts;

	public List<String> getCheckOuts() {
		return checkOuts;
	}

	public void setCheckOuts(List<String> checkOuts) {
		this.checkOuts = checkOuts;
	}
	
	
}
