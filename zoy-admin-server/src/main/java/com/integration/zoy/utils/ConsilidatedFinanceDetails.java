package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class ConsilidatedFinanceDetails {
	@SerializedName("customerId")
	private String userId;
	
	@SerializedName("transactionDate")
    private Timestamp userPaymentTimestamp;
	
	@SerializedName("transactionNumber")
    private String userPaymentBankTransactionId;
	
	@SerializedName("customerName")
    private String userPersonalName;
	
	@SerializedName("creditAmount")
	private BigDecimal totalAmount;
	
	@SerializedName("debitAmount")
	private BigDecimal debitAmount;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getUserPaymentTimestamp() {
		return userPaymentTimestamp;
	}

	public void setUserPaymentTimestamp(Timestamp userPaymentTimestamp) {
		this.userPaymentTimestamp = userPaymentTimestamp;
	}

	public String getUserPersonalName() {
		return userPersonalName;
	}

	public void setUserPersonalName(String userPersonalName) {
		this.userPersonalName = userPersonalName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getUserPaymentBankTransactionId() {
		return userPaymentBankTransactionId;
	}

	public void setUserPaymentBankTransactionId(String userPaymentBankTransactionId) {
		this.userPaymentBankTransactionId = userPaymentBankTransactionId;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}
	
}
