package com.integration.zoy.utils;

import java.math.BigDecimal;
import com.google.gson.annotations.SerializedName;

public class TenentRefund {

	@SerializedName("paymentDate")
	private String paymentDate;

	@SerializedName("customerName")
	private String customerName;

	@SerializedName("tenantMobileNum")
	private String tenantMobileNum;

	@SerializedName("pgPropertyName")
	private String pgPropertyName;

	@SerializedName("userPgPropertyAddress")
	private String userPgPropertyAddress;

	@SerializedName("bookingId")
	private String bookingId;

	@SerializedName("refundTitle")
	private String refundTitle;

	@SerializedName("refundableAmount")
	private String refundableAmount;

	@SerializedName("amountPaid")
	private BigDecimal amountPaid;

	@SerializedName("transactionNumber")
	private String transactionNumber;

	@SerializedName("paymentStatus")
	private String paymentStatus;


	public String getRefundableAmount() {
		return refundableAmount;
	}

	public void setRefundableAmount(String refundableAmount) {
		this.refundableAmount = refundableAmount;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
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

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getTransactionNumber() {
		return transactionNumber;
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
