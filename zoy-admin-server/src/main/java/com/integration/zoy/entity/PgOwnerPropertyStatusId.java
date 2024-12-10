package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PgOwnerPropertyStatusId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "pg_owner_id", nullable = false, length = 36)
	private String pgOwnerId;

	@Column(name = "property_id", nullable = false, length = 36)
	private String propertyId;

	public PgOwnerPropertyStatusId() {}

	public PgOwnerPropertyStatusId(String pgOwnerId, String propertyId) {
		this.pgOwnerId = pgOwnerId;
		this.propertyId = propertyId;
	}

	public String getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(String pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PgOwnerPropertyStatusId)) return false;
		PgOwnerPropertyStatusId that = (PgOwnerPropertyStatusId) o;
		return Objects.equals(pgOwnerId, that.pgOwnerId) &&
				Objects.equals(propertyId, that.propertyId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pgOwnerId, propertyId);
	}
}
