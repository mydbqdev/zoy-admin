package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyPgNoRentalAgreementDto {

	@SerializedName("noRentalAgreementId")
	private String noRentalAgreementId;

	@SerializedName("noRentalAgreementDays")
	private int noRentalAgreementDays;

	@SerializedName("effectiveDate")
	private String effectiveDate;

	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;
	
	@SerializedName("comments")
	private String comments;
	
	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

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

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
