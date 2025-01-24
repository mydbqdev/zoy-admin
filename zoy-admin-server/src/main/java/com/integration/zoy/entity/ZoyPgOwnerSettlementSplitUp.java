package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "zoy_pg_owner_settlement_split_up", schema = "pgowners")
public class ZoyPgOwnerSettlementSplitUp {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "pg_owner_settlement_split_up_id", nullable = false, length = 36)
	private String pgOwnerSettlementSplitUpId;
	
	@Column(name = "pg_owner_settlement_id", updatable = false, nullable = false, unique = true, length = 36)
	private String pgOwnerSettlementId;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "booking_id", nullable = false)
	private String bookingId;

	@Column(name = "payment_id", nullable = false)
	private String paymentId;

	public String getPgOwnerSettlementId() {
		return pgOwnerSettlementId;
	}

	public void setPgOwnerSettlementId(String pgOwnerSettlementId) {
		this.pgOwnerSettlementId = pgOwnerSettlementId;
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

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	
}
