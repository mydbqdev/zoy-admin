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
	private double creditAmount;

	@SerializedName("debitAmount")
	private double debitAmount;
	
	@SerializedName("pgName")
	private String pgName;
	
	@SerializedName("contactNum")
	private String contactNum;

	
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

	public double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getContactNum() {
		return contactNum;
	}

	public void setContactNum(String contactNum) {
		this.contactNum = contactNum;
	}

	
}
