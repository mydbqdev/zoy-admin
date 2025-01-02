package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class TenantDetails {
	@SerializedName("tenantName")
	private String tenantName;
	@SerializedName("contactNumber")
    private String contactNumber;
	@SerializedName("userEmail")
    private String userEmail;
	@SerializedName("status")
    private String status;
	@SerializedName("appUser")
    private String appUser;
	@SerializedName("ekycStatus")
    private String ekycStatus;
	@SerializedName("registeredDate")
    private String registeredDate;
	@SerializedName("tenantId")
	private String tenantId;
	
	public TenantDetails(String tenantName, String contactNumber, String userEmail, String status, String appUser,
			String ekycStatus, String registeredDate,String tenantId) {
		super();
		this.tenantName = tenantName;
		this.contactNumber = contactNumber;
		this.userEmail = userEmail;
		this.status = status;
		this.appUser = appUser;
		this.ekycStatus = ekycStatus;
		this.registeredDate =  registeredDate;
		this.tenantId=tenantId;
	}
	
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAppUser() {
		return appUser;
	}
	public void setAppUser(String appUser) {
		this.appUser = appUser;
	}
	public String getEkycStatus() {
		return ekycStatus;
	}
	public void setEkycStatus(String ekycStatus) {
		this.ekycStatus = ekycStatus;
	}

	public String getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	

    
}
