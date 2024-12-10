package com.integration.zoy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AcquirerData {

	@JsonProperty("bank_transaction_id")
	String bankTransactionId;


	public void setBankTransactionId(String bankTransactionId) {
		this.bankTransactionId = bankTransactionId;
	}
	public String getBankTransactionId() {
		return bankTransactionId;
	}

}