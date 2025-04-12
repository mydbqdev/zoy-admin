package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class RentalAgreementDocDto {

	@SerializedName("rentalAgreementDocId")
	private String rentalAgreementDocId;

	@SerializedName("rentalAgreementDoc")
	private String rentalAgreementDoc;

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
