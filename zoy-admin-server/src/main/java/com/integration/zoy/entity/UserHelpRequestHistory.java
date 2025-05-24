package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity 
@Table(name = "user_help_request_history", schema = "pgusers")
public class UserHelpRequestHistory {
	@Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "user_help_request_history_id", updatable = false, nullable = false, unique = true, length = 36)
    private String id;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "user_help_request_id")
    private String userHelpRequestId;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "request_status")
    private String requestStatus;
    
    @Column(name = "user_email")
    private String userEmail;

	@CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
	
	public String getUserHelpRequestId() {
		return userHelpRequestId;
	}

	public void setUserHelpRequestId(String userHelpRequestId) {
		this.userHelpRequestId = userHelpRequestId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	
}
