package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "pg_owner_user_status", schema = "pgcommon")
public class PgOwnerUserStatus {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "pg_owner_user_status_id", nullable = false, length = 36)
	private String pgOwnerUserStatusId;

	@Column(name = "user_bookings_id", nullable = false, length = 36)
	private String userBookingsId;

	@Column(name = "user_id", nullable = false, length = 36)
	private String userId;

	@Column(name = "pg_owner_id", nullable = false, length = 36)
	private String pgOwnerId;

	@Column(name = "property_id", nullable = false, length = 36)
	private String propertyId;

	@Column(name = "bed_id", nullable = false, length = 36)
	private String bedId;

	@Column(name = "pg_tenant_status", nullable = false)
	private boolean pgTenantStatus = false;

	public String getPgOwnerUserStatusId() {
		return pgOwnerUserStatusId;
	}

	public void setPgOwnerUserStatusId(String pgOwnerUserStatusId) {
		this.pgOwnerUserStatusId = pgOwnerUserStatusId;
	}

	public String getUserBookingsId() {
		return userBookingsId;
	}

	public void setUserBookingsId(String userBookingsId) {
		this.userBookingsId = userBookingsId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getBedId() {
		return bedId;
	}

	public void setBedId(String bedId) {
		this.bedId = bedId;
	}

	public boolean isPgTenantStatus() {
		return pgTenantStatus;
	}

	public void setPgTenantStatus(boolean pgTenantStatus) {
		this.pgTenantStatus = pgTenantStatus;
	}
}
