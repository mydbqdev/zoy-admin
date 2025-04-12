package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity 
@Table(name = "follow_ups", schema = "pgadmin")
public class FollowUps {
	@Id
	@GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "follow_up_id", nullable = false, length = 50)
    private String followUpId;

    @Column(name = "inquiry_id", nullable = false, length = 50)
    private String inquiryId;

    @Column(name = "follow_up_date")
    private Timestamp followUpDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "reminder_set")
    private Boolean reminderSet;

    @Column(name = "reminder_date")
    private Timestamp reminderDate;

    @Column(name = "status")
    private String status;
    
    @Column(name = "user_email")
    private String userEmail;

	public String getFollowUpId() {
		return followUpId;
	}

	public void setFollowUpId(String followUpId) {
		this.followUpId = followUpId;
	}

	public String getInquiryId() {
		return inquiryId;
	}

	public void setInquiryId(String inquiryId) {
		this.inquiryId = inquiryId;
	}

	public Timestamp getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Timestamp followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean getReminderSet() {
		return reminderSet;
	}

	public void setReminderSet(Boolean reminderSet) {
		this.reminderSet = reminderSet;
	}

	public Timestamp getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Timestamp reminderDate) {
		this.reminderDate = reminderDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}



}
