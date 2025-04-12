package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class ZoyPgUserLoginDetailsModel {

	@SerializedName("userEmail")
	private String userEmail;

	@SerializedName("password")
	private String password;

	// Optional nested object
	@SerializedName("pgSalesMaster")
	private ZoyPgSalesMasterModel pgSalesMaster;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ZoyPgSalesMasterModel getPgSalesMaster() {
		return pgSalesMaster;
	}

	public void setPgSalesMaster(ZoyPgSalesMasterModel pgSalesMaster) {
		this.pgSalesMaster = pgSalesMaster;
	}
}
