package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class RegisterTenantsDTO {
	
	@SerializedName("tenantName")
    private String tenantName;

    @SerializedName("tenantContactNumber")
    private String tenantContactNumber;

    @SerializedName("tenantEmailAddress")
    private String tenantEmailAddress;

    @SerializedName("registrationDate")
    private Timestamp registrationDate;

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantContactNumber() {
		return tenantContactNumber;
	}

	public void setTenantContactNumber(String tenantContactNumber) {
		this.tenantContactNumber = tenantContactNumber;
	}

	public String getTenantEmailAddress() {
		return tenantEmailAddress;
	}

	public void setTenantEmailAddress(String tenantEmailAddress) {
		this.tenantEmailAddress = tenantEmailAddress;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

}
