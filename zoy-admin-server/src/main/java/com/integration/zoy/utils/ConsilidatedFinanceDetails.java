package com.integration.zoy.utils;

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
	private String creditAmount;
	
	@SerializedName("debitAmount")
	private String debitAmount;

	
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

	public String getUserPaymentBankTransactionId() {
		return userPaymentBankTransactionId;
	}

	public void setUserPaymentBankTransactionId(String userPaymentBankTransactionId) {
		this.userPaymentBankTransactionId = userPaymentBankTransactionId;
	}

	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(String debitAmount) {
		this.debitAmount = debitAmount;
	}

}
