package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class FilterData {
	@SerializedName("tenantName")
	String tenantName;

	@SerializedName("transactionStatus")
	String transactionStatus;

	@SerializedName("modeOfPayment")
	String modeOfPayment;

	@SerializedName("zoyCode")
	String zoyCode;

	@SerializedName("ownerName")
	String ownerName;

	@SerializedName("pgName")
	String pgName;

	@SerializedName("payeeType")
	String payeeType;

	@SerializedName("payeeName")
	String payeeName;

	@SerializedName("payerType")
	String payerType;

	@SerializedName("payerName")
	String payerName;

	@SerializedName("tenantContactNum")
	String tenantContactNum;

	@SerializedName("transactionNumber")
	String transactionNumber;

	@SerializedName("ownerEmail")
	String ownerEmail;

	@SerializedName("bedNumber")
	String bedNumber;

	@SerializedName("pgAddress")
	String pgAddress;

	@SerializedName("bookingId")
	String bookinId;

	@SerializedName("refundTitle")
	String refundTitle;

	@SerializedName("refundAmount")
	String refundAmount;

	@SerializedName("overallRating")
	String overallRating;

	@SerializedName("reviewDate")
	String reviewDate;

	@SerializedName("Cleanliness")
	String Cleanliness;

	@SerializedName("Accommodation")
	String Accommodation;

	@SerializedName("Amenities")
	String Amenities;

	@SerializedName("Maintenance")
	String Maintenance;

	@SerializedName("valueForMoney")
	String valueForMoney;
	
	public String getCleanliness() {
		return Cleanliness;
	}

	public void setCleanliness(String cleanliness) {
		Cleanliness = cleanliness;
	}

	public String getAccommodation() {
		return Accommodation;
	}

	public void setAccommodation(String accommodation) {
		Accommodation = accommodation;
	}

	public String getAmenities() {
		return Amenities;
	}

	public void setAmenities(String amenities) {
		Amenities = amenities;
	}

	public String getMaintenance() {
		return Maintenance;
	}

	public void setMaintenance(String maintenance) {
		Maintenance = maintenance;
	}

	public String getValueForMoney() {
		return valueForMoney;
	}

	public void setValueForMoney(String valueForMoney) {
		this.valueForMoney = valueForMoney;
	}

	public String getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(String overallRating) {
		this.overallRating = overallRating;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getRefundTitle() {
		return refundTitle;
	}

	public void setRefundTitle(String refundTitle) {
		this.refundTitle = refundTitle;
	}

	public String getBookinId() {
		return bookinId;
	}

	public void setBookinId(String bookinId) {
		this.bookinId = bookinId;
	}

	public String getPgAddress() {
		return pgAddress;
	}

	public void setPgAddress(String pgAddress) {
		this.pgAddress = pgAddress;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}

	public String getZoyCode() {
		return zoyCode;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public String getPayeeType() {
		return payeeType;
	}

	public void setPayeeType(String payeeType) {
		this.payeeType = payeeType;
	}

	public String getPayerType() {
		return payerType;
	}

	public void setPayerType(String payerType) {
		this.payerType = payerType;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getPayerName() {
		return payerName;
	}

	public String getTenantContactNum() {
		return tenantContactNum;
	}

	public void setTenantContactNum(String tenantContactNum) {
		this.tenantContactNum = tenantContactNum;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

}
