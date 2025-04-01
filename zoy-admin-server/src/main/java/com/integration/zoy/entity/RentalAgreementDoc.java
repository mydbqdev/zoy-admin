package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class RentalAgreementDoc {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "rental_Agreement_doc_id", nullable = false, unique = true, length = 36)
	private String rentalAgreementDocId;

	@Column(name = "rental_Agreement_doc")
	private String rentalAgreementDoc;

	@Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "approved_by")
	private String approvedBy;

	// Getters and Setters

	public Boolean getIsApproved() {
		return isApproved;
	}

	public String getRentalAgreementDocId() {
		return rentalAgreementDocId;
	}

	public void setRentalAgreementDocId(String rentalAgreementDocId) {
		this.rentalAgreementDocId = rentalAgreementDocId;
	}

	public String getRentalAgreementDoc() {
		return rentalAgreementDoc;
	}

	public void setRentalAgreementDoc(String rentalAgreementDoc) {
		this.rentalAgreementDoc = rentalAgreementDoc;
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
}
