package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class ConsilidatedFinanceDetails {
	@SerializedName("payerPayeeType")
	private String payerPayeeType;
	
	@SerializedName("transactionDate")
    private Timestamp userPaymentTimestamp;
	
	@SerializedName("transactionNumber")
    private String userPaymentBankTransactionId;
	
	@SerializedName("payerPayeeName")
    private String payerPayeeName;
	
	@SerializedName("creditAmount")
	private BigDecimal creditAmount;
	
	@SerializedName("debitAmount")
	private BigDecimal debitAmount;

	
	public String getPayerPayeeType() {
		return payerPayeeType;
	}

	public void setPayerPayeeType(String payerPayeeType) {
		this.payerPayeeType = payerPayeeType;
	}

	public Timestamp getUserPaymentTimestamp() {
		return userPaymentTimestamp;
	}

	public void setUserPaymentTimestamp(Timestamp userPaymentTimestamp) {
		this.userPaymentTimestamp = userPaymentTimestamp;
	}

	public String getPayerPayeeName() {
		return payerPayeeName;
	}

	public void setPayerPayeeName(String payerPayeeName) {
		this.payerPayeeName = payerPayeeName;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
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
