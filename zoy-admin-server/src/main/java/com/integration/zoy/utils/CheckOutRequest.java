package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class CheckOutRequest {
	@SerializedName("checkoutDate")
	Timestamp checkoutDate;

	@SerializedName("isAccepted")
	boolean isAccepted;


	public void setCheckoutDate(Timestamp checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	public Timestamp getCheckoutDate() {
		return checkoutDate;
	}

	public void setIsAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}
	public boolean getIsAccepted() {
		return isAccepted;
	}

}
