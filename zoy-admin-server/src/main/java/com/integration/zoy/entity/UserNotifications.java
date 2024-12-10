package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "user_notifications", schema = "pgusers")
@IdClass(UserNotificationsId.class)
public class UserNotifications {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "notification_mode_id")
    private String notificationModeId;

    
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
}
