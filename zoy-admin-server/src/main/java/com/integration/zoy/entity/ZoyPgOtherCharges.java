package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "zoy_pg_other_charges", schema = "pgowners")
public class ZoyPgOtherCharges {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "other_charges_id", updatable = false, nullable = false, unique = true, length = 36)
	private String otherChargesId;

	@Column(name = "other_gst", nullable = false, precision = 10, scale = 2)
	private BigDecimal otherGst;

	@Column(name = "document_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal documentCharges;

	// Getters and Setters

	public String getOtherChargesId() {
		return otherChargesId;
	}

	public void setOtherChargesId(String otherChargesId) {
		this.otherChargesId = otherChargesId;
	}

	public BigDecimal getOtherGst() {
		return otherGst;
	}

	public void setOtherGst(BigDecimal otherGst) {
		this.otherGst = otherGst;
	}

	public BigDecimal getDocumentCharges() {
		return documentCharges;
	}

	public void setDocumentCharges(BigDecimal documentCharges) {
		this.documentCharges = documentCharges;
	}
}