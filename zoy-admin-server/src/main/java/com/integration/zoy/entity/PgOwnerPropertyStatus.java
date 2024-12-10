package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "pg_owner_property_status", schema = "pgcommon")
public class PgOwnerPropertyStatus {


	@EmbeddedId
	private PgOwnerPropertyStatusId id;

	@Column(name = "status", nullable = false)
	private boolean status = false;

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
