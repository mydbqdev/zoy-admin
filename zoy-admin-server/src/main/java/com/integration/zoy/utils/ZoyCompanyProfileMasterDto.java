package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyCompanyProfileMasterDto {
	@SerializedName("companyProfileId")
	private String companyProfileId;
	
	@SerializedName("type")
    private String type;
	
	@SerializedName("pinCode")
    private String pinCode;
	
	@SerializedName("state")
    private String state;
	
	@SerializedName("city")
    private String city;
	
	@SerializedName("addressLineOne")
    private String addressLineOne;
	
	@SerializedName("addressLineTwo")
    private String addressLineTwo;
	
	@SerializedName("addressLineThree")
    private String addressLineThree;
	
	@SerializedName("contactNumberOne")
    private String contactNumberOne;
	
	@SerializedName("contactNumberTwo")
    private String contactNumberTwo;
	
	@SerializedName("emailOne")
    private String emailOne;
	
	@SerializedName("emailTwo")
    private String emailTwo;
	
	@SerializedName("status")
    private String status;
	
	public String getCompanyProfileId() {
		return companyProfileId;
	}
	public void setCompanyProfileId(String companyProfileId) {
		this.companyProfileId = companyProfileId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddressLineOne() {
		return addressLineOne;
	}
	public void setAddressLineOne(String addressLineOne) {
		this.addressLineOne = addressLineOne;
	}
	public String getAddressLineTwo() {
		return addressLineTwo;
	}
	public void setAddressLineTwo(String addressLineTwo) {
		this.addressLineTwo = addressLineTwo;
	}
	public String getAddressLineThree() {
		return addressLineThree;
	}
	public void setAddressLineThree(String addressLineThree) {
		this.addressLineThree = addressLineThree;
	}
	public String getContactNumberOne() {
		return contactNumberOne;
	}
	public void setContactNumberOne(String contactNumberOne) {
		this.contactNumberOne = contactNumberOne;
	}
	public String getContactNumberTwo() {
		return contactNumberTwo;
	}
	public void setContactNumberTwo(String contactNumberTwo) {
		this.contactNumberTwo = contactNumberTwo;
	}
	public String getEmailOne() {
		return emailOne;
	}
	public void setEmailOne(String emailOne) {
		this.emailOne = emailOne;
	}
	public String getEmailTwo() {
		return emailTwo;
	}
	public void setEmailTwo(String emailTwo) {
		this.emailTwo = emailTwo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
}
