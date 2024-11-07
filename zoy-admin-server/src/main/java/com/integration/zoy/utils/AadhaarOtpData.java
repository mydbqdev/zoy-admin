package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class AadhaarOtpData {
	@SerializedName("code")
	int code;

	@SerializedName("timestamp")
	int timestamp;

	@SerializedName("transaction_id")
	String transactionId;

	@SerializedName("sub_code")
	String subCode;

	@SerializedName("message")
	String message;


	public void setCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	public int getTimestamp() {
		return timestamp;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionId() {
		return transactionId;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}
	public String getSubCode() {
		return subCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
