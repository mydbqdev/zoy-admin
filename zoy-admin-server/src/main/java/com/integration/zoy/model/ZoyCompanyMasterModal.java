package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyCompanyMasterModal {
@JsonProperty("company_name")
private String companyName;

@JsonProperty("gst_number")
private String gstNumber;

@JsonProperty("pan_number")
private String panNumber;

@JsonProperty("url")
private String url;

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
@Override
public String toString() {
	return "ZoyCompanyMasterModal [companyName=" + companyName + ", gstNumber=" + gstNumber + ", panNumber=" + panNumber
			+ ", url=" + url + "]";
}

}
