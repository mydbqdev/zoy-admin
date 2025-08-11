package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class TenentRefund {

	@SerializedName("paymentDate")
	private Timestamp paymentDate;

	@SerializedName("customerName")
	private String customerName;

	@SerializedName("tenantMobileNum")
	private String tenantMobileNum;

	@SerializedName("PgPropertyName")
	private String pgPropertyName;

	@SerializedName("userPgPropertyAddress")
	private String userPgPropertyAddress;

	@SerializedName("bookingId")
	private String bookingId;

	@SerializedName("refundTitle")
	private String refundTitle;

	@SerializedName("refundableAmount")
	private double refundableAmount;

	@SerializedName("amountPaid")
	private double amountPaid;

	@SerializedName("transactionNumber")
	private String transactionNumber;

	@SerializedName("paymentStatus")
	private String paymentStatus;
	
	@SerializedName("tenantAccountNumber")
	private String tenantAccountNumber ;
	
	@SerializedName("tenantIfscCode")
	private String tenantIfscCode ;

	public double getRefundableAmount() {
		return refundableAmount;
	}

	public void setRefundableAmount(double refundableAmount) {
		this.refundableAmount = refundableAmount;
	}

	public Timestamp getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getTenantMobileNum() {
		return tenantMobileNum;
	}

	public void setTenantMobileNum(String tenantMobileNum) {
		this.tenantMobileNum = tenantMobileNum;
	}

	public String getPgPropertyName() {
		return pgPropertyName;
	}

	public void setPgPropertyName(String pgPropertyName) {
		this.pgPropertyName = pgPropertyName;
	}

	public String getUserPgPropertyAddress() {
		return userPgPropertyAddress;
	}

	public void setUserPgPropertyAddress(String userPgPropertyAddress) {
		this.userPgPropertyAddress = userPgPropertyAddress;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getRefundTitle() {
		return refundTitle;
	}

	public void setRefundTitle(String refundTitle) {
		this.refundTitle = refundTitle;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public String getTenantAccountNumber() {
		return tenantAccountNumber;
	}

	public void setTenantAccountNumber(String tenantAccountNumber) {
		this.tenantAccountNumber = tenantAccountNumber;
	}

	public String getTenantIfscCode() {
		return tenantIfscCode;
	}

	public void setTenantIfscCode(String tenantIfscCode) {
		this.tenantIfscCode = tenantIfscCode;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}
