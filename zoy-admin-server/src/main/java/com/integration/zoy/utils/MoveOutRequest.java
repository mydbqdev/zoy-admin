package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class MoveOutRequest {

	@SerializedName("outDate")
	Timestamp outDate;

	@SerializedName("reason")
	String reason;

	@SerializedName("reqRaisedDate")
	Timestamp reqRaisedDate;

	public Timestamp getOutDate() {
		return outDate;
	}

	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Timestamp getReqRaisedDate() {
		return reqRaisedDate;
	}

	public void setReqRaisedDate(Timestamp reqRaisedDate) {
		this.reqRaisedDate = reqRaisedDate;
	}

	
	


}
