package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShareTypeId {
	@JsonProperty("id")
	String id;

	@JsonProperty("shareType")
    private String shareType;

	@JsonProperty("shareOccupancyCount")
    private Integer shareOccupancyCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
