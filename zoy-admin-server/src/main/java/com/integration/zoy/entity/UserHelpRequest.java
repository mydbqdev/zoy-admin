package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_help_request", schema = "pgusers")
public class UserHelpRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_help_request_id", nullable = false, updatable = false)
	private String userHelpRequestId;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "property_id", nullable = false, length = 36)
    private String propertyId;
    
    @Column(name = "booking_id", length = 36)
    private String bookingId;

    @Column(name = "categories_id", nullable = false, length = 36)
    private String categoriesId;

    @Column(name = "description")
    private String description;

    @Column(name = "urgency", nullable = false)
    private boolean urgency = false;

    @Column(name = "request_status", nullable = false)
    private String requestStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private Timestamp updateAt;
    
    @Column(name = "partner_id",  length = 36)
    private String partnerId;
    
    @Column(name = "sub_categories_id", length = 36)
    private String subCategoriesId;

	@Column(name = "assign_to_email")
	private String assignedToEmail;
	
	@Column(name = "assign_to_name")
	private String assignedToName;

	public String getUserHelpRequestId() {
		return userHelpRequestId;
	}

	public void setUserHelpRequestId(String userHelpRequestId) {
		this.userHelpRequestId = userHelpRequestId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getCategoriesId() {
		return categoriesId;
	}

	public void setCategoriesId(String categoriesId) {
		this.categoriesId = categoriesId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isUrgency() {
		return urgency;
	}

	public void setUrgency(boolean urgency) {
		this.urgency = urgency;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	
	public String getAssignedToEmail() {
		return assignedToEmail;
	}

	public void setAssignedToEmail(String assignedToEmail) {
		this.assignedToEmail = assignedToEmail;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Timestamp updateAt) {
		this.updateAt = updateAt;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getSubCategoriesId() {
		return subCategoriesId;
	}

	public void setSubCategoriesId(String subCategoriesId) {
		this.subCategoriesId = subCategoriesId;
	}
	
	
}
