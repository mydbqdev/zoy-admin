package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class VendorPaymentsGst {

	@SerializedName("transactionDate")
	private Timestamp transactionDate;

	@SerializedName("transactionNo")
	private String transactionNo;

	@SerializedName("pgId")
	private String pgId;

	@SerializedName("pgName")
	private String pgName;

	@SerializedName("totalAmount")
	private double totalAmount;

	@SerializedName("gstAmount")
	private double gstAmount;

	@SerializedName("basicAmount")
	private double basicAmount;

	@SerializedName("paymentMethod")
	private String paymentMethod;

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getPgId() {
		return pgId;
	}

	public void setPgId(String pgId) {
		this.pgId = pgId;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public double getBasicAmount() {
		return basicAmount;
	}

	public void setBasicAmount(double basicAmount) {
		this.basicAmount = basicAmount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
