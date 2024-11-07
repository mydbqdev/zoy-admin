package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomTypeId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("roomTypeName")
	private String roomTypeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	


}
