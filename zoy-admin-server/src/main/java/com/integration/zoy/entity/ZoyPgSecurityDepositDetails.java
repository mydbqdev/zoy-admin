package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "zoy_pg_security_deposit_details", schema = "pgowners")
public class ZoyPgSecurityDepositDetails {

	@Id
	@GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "security_deposit_id", nullable = false, length = 36)
	private String securityDepositId;

	@Column(name = "security_deposit_min", precision = 10, scale = 2)
	private BigDecimal securityDepositMin;
	
	@Column(name = "security_deposit_max", precision = 10, scale = 2)
	private BigDecimal securityDepositMax;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "update_at")
	@UpdateTimestamp
	private Timestamp updatedAt;

	// Getters and Setters
	public String getSecurityDepositId() {
		return securityDepositId;
	}

	public void setSecurityDepositId(String securityDepositId) {
		this.securityDepositId = securityDepositId;
	}

	public BigDecimal getSecurityDepositMin() {
		return securityDepositMin;
	}

	public void setSecurityDepositMin(BigDecimal securityDepositMin) {
		this.securityDepositMin = securityDepositMin;
	}

	public BigDecimal getSecurityDepositMax() {
		return securityDepositMax;
	}

	public void setSecurityDepositMax(BigDecimal securityDepositMax) {
		this.securityDepositMax = securityDepositMax;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}