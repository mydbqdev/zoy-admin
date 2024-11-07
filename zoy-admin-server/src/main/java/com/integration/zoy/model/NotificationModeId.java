package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationModeId {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("notificationModeName")
	private String notificationModeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNotificationModeName() {
		return notificationModeName;
	}

	public void setNotificationModeName(String notificationModeName) {
		this.notificationModeName = notificationModeName;
	}

	

	


}
