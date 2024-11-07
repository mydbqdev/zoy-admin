package com.integration.zoy.model;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShareType {

	@JsonProperty("shareType")
    private String shareType;

	@JsonProperty("shareOccupancyCount")
    private Integer shareOccupancyCount;

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public Integer getShareOccupancyCount() {
		return shareOccupancyCount;
	}

	public void setShareOccupancyCount(Integer shareOccupancyCount) {
		this.shareOccupancyCount = shareOccupancyCount;
	}
	
	
}
