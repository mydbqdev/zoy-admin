package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "pg_owner_property_status", schema = "pgcommon")
public class PgOwnerPropertyStatus {

	@EmbeddedId
	private PgOwnerPropertyStatusId id;

	@Column(name = "status", nullable = false)
	private boolean status = false;

	@Column(name = "status_type")
	private String statusType;

	@CreationTimestamp
	@Column(name = "updated_timestamp")
	private Timestamp updatedOn;

	@Column(name = "status_reason")
	private String statusReason;

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public PgOwnerPropertyStatusId getId() {
		return id;
	}

	public void setId(PgOwnerPropertyStatusId id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
