package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class RentalAgreementDocDto {

	@SerializedName("rentalAgreementDocId")
	private String rentalAgreementDocId;

	@SerializedName("rentalAgreementDoc")
	private String rentalAgreementDoc;

	@SerializedName("effectiveDate")
	private String effectiveDate;

	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
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

}
