package com.integration.zoy.entity;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "zoy_pg_token_details", schema = "pgowners")
public class ZoyPgTokenDetails {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "token_id", nullable = false, length = 36)
	private String tokenId;

	@Column(name = "fixed_token", precision = 10, scale = 2)
	private BigDecimal fixedToken;

	@Column(name = "variable_token", precision = 10, scale = 2)
	private BigDecimal variableToken;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private Timestamp updatedAt;

	// Getters and Setters
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public BigDecimal getFixedToken() {
		return fixedToken;
	}

	public void setFixedToken(BigDecimal fixedToken) {
		this.fixedToken = fixedToken;
	}

	public BigDecimal getVariableToken() {
		return variableToken;
	}

	public void setVariableToken(BigDecimal variableToken) {
		this.variableToken = variableToken;
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

