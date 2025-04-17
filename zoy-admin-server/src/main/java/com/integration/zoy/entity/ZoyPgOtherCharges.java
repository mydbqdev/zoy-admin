package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_other_charges", schema = "pgowners")
public class ZoyPgOtherCharges {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "other_charges_id", updatable = false, nullable = false, unique = true, length = 36)
	private String otherChargesId;

	@Column(name = "owner_document_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal ownerDocumentCharges;

	@Column(name = "tenant_document_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal tenantDocumentCharges;
	
	@Column(name = "owner_ekyc_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal ownerEkycCharges;
	
	@Column(name = "tenant_ekyc_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal tenantEkycCharges;
	
	@Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="approved_by")
	private String approvedBy;
	
	@Column(name="comments")
	private String comments;

	// Getters and Setters

	public String getOtherChargesId() {
		return otherChargesId;
	}

	public void setOtherChargesId(String otherChargesId) {
		this.otherChargesId = otherChargesId;
	}

	public BigDecimal getOwnerDocumentCharges() {
		return ownerDocumentCharges;
	}

	public void setOwnerDocumentCharges(BigDecimal ownerDocumentCharges) {
		this.ownerDocumentCharges = ownerDocumentCharges;
	}

	public BigDecimal getTenantDocumentCharges() {
		return tenantDocumentCharges;
	}

	public void setTenantDocumentCharges(BigDecimal tenantDocumentCharges) {
		this.tenantDocumentCharges = tenantDocumentCharges;
	}

	public BigDecimal getOwnerEkycCharges() {
		return ownerEkycCharges;
	}

	public void setOwnerEkycCharges(BigDecimal ownerEkycCharges) {
		this.ownerEkycCharges = ownerEkycCharges;
	}

	public BigDecimal getTenantEkycCharges() {
		return tenantEkycCharges;
	}

	public void setTenantEkycCharges(BigDecimal tenantEkycCharges) {
		this.tenantEkycCharges = tenantEkycCharges;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}