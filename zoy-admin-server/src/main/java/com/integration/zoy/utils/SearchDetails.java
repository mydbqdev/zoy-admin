package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SearchDetails {

	@SerializedName("searchDetails")
	private List<SearchData> searchDetails;

	public List<SearchData> getSearchDetails() {
		return searchDetails;
	}

	public void setSearchDetails(List<SearchData> searchDetails) {
		this.searchDetails = searchDetails;
	}
	
	
	
}
