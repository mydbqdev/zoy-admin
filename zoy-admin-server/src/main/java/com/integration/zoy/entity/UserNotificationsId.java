package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserNotificationsId implements Serializable {
	 private static final long serialVersionUID = 1L;
    private String userId;
    private String notificationModeId;

    
    public UserNotificationsId() {}

    public UserNotificationsId(String userId, String notificationModeId) {
        this.userId = userId;
        this.notificationModeId = notificationModeId;
    }

   
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationModeId() {
        return notificationModeId;
    }

    public void setNotificationModeId(String notificationModeId) {
        this.notificationModeId = notificationModeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNotificationsId)) return false;
        UserNotificationsId that = (UserNotificationsId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(notificationModeId, that.notificationModeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notificationModeId);
    }
}
