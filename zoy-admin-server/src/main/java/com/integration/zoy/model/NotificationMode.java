package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationMode {
	@JsonProperty("notificationModeName")
	private String notificationModeName;


	public String getNotificationModeName() {
		return notificationModeName;
	}

	public void setNotificationModeName(String notificationModeName) {
		this.notificationModeName = notificationModeName;
	}

	

	


}
