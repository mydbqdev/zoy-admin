package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomType {
	@JsonProperty("roomTypeName")
	private String roomTypeName;

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	


}
