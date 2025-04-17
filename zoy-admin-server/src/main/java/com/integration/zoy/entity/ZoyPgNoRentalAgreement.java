package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_no_rental_agreement", schema = "pgowners")
public class ZoyPgNoRentalAgreement {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "no_rental_agreement_id", nullable = false, length = 36)
	private String noRentalAgreementId;

	@Column(name = "no_rental_agreement_days", nullable = false)
	private int noRentalAgreementDays;

	@Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "approved_by")
	private String approvedBy;
	
	@Column(name="comments")
	private String comments;

	public String getNoRentalAgreementId() {
		return noRentalAgreementId;
	}

	public void setNoRentalAgreementId(String noRentalAgreementId) {
		this.noRentalAgreementId = noRentalAgreementId;
	}

	public int getNoRentalAgreementDays() {
		return noRentalAgreementDays;
	}

	public void setNoRentalAgreementDays(int noRentalAgreementDays) {
		this.noRentalAgreementDays = noRentalAgreementDays;
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

	@Override
	public String toString() {
		return "ZoyPgNoRentalAgreement [noRentalAgreementId=" + noRentalAgreementId + ", noRentalAgreementDays="
				+ noRentalAgreementDays + ", isApproved=" + isApproved + ", effectiveDate=" + effectiveDate
				+ ", createdBy=" + createdBy + ", approvedBy=" + approvedBy + ", comments=" + comments + "]";
	}

}
