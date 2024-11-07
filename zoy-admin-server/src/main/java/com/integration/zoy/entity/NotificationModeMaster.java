package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "notification_mode_master", schema = "pgusers")
public class NotificationModeMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "notification_mode_id", updatable = false, nullable = false, unique = true, length = 36)
    private String notificationModeId;

    @Column(name = "notification_mod_name")
    private String notificationModName;

   
    public String getNotificationModeId() {
        return notificationModeId;
    }

    public void setNotificationModeId(String notificationModeId) {
        this.notificationModeId = notificationModeId;
    }

    public String getNotificationModName() {
        return notificationModName;
    }

    public void setNotificationModName(String notificationModName) {
        this.notificationModName = notificationModName;
    }
}