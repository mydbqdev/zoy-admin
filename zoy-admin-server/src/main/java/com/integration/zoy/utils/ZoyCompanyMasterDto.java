package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyCompanyMasterDto {
	@SerializedName("companyMasterId")
	private String companyMasterId;
	@SerializedName("companyName")
	private String companyName;
	@SerializedName("gstNumber")
	private String gstNumber;
	@SerializedName("panNumber")
	private String panNumber;
	@SerializedName("url")
	private String url;
	@SerializedName("logo")
	private byte[] logo;
	public String getCompanyMasterId() {
		return companyMasterId;
	}
	public void setCompanyMasterId(String companyMasterId) {
		this.companyMasterId = companyMasterId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getGstNumber() {
		return gstNumber;
	}
	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public byte[] getLogo() {
		return logo;
	}
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

}
