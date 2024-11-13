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
	private BigDecimal totalAmount;
	
	@SerializedName("gstAmount")
	private BigDecimal gstAmount;
	
	@SerializedName("basicAmount")
	private BigDecimal basicAmount;
	
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getBasicAmount() {
		return basicAmount;
	}

	public void setBasicAmount(BigDecimal basicAmount) {
		this.basicAmount = basicAmount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
}
