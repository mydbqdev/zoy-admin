package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "zoy_pg_owner_settlement_status", schema = "pgowners")
public class ZoyPgOwnerSettlementStatus {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "pg_owner_settlement_id", updatable = false, nullable = false, unique = true, length = 36)
	private String pgOwnerSettlementId;

//	@Column(name = "payment_id", nullable = false)
//	private String paymentId;

	@Column(name = "owner_id", nullable = false)
	private String ownerId;

	@Column(name = "property_id", nullable = false)
	private String propertyId;

	@Column(name = "settlement_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal settlementAmount;

	@Column(name = "payment_process_date", nullable = false)
	private Timestamp paymentProcessDate;

	@Column(name = "is_approved", nullable = false)
	private Boolean isApproved = false;
	
	@Column(name = "is_rejected")
	private Boolean isRejected = false;
	
	@Column(name = "rejected_reason")
	private String rejectedReason;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "update_at")
	@UpdateTimestamp
	private Timestamp updatedAt;

	// Getters and Setters

	public String getPgOwnerSettlementId() {
		return pgOwnerSettlementId;
	}

	public void setPgOwnerSettlementId(String pgOwnerSettlementId) {
		this.pgOwnerSettlementId = pgOwnerSettlementId;
	}

//	public String getPaymentId() {
//		return paymentId;
//	}
//
//	public void setPaymentId(String paymentId) {
//		this.paymentId = paymentId;
//	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public Timestamp getPaymentProcessDate() {
		return paymentProcessDate;
	}

	public void setPaymentProcessDate(Timestamp paymentProcessDate) {
		this.paymentProcessDate = paymentProcessDate;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public Boolean getIsRejected() {
		return isRejected;
	}

	public void setIsRejected(Boolean isRejected) {
		this.isRejected = isRejected;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
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
