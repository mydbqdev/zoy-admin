package com.integration.zoy.model;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class TenantResportsDTO {

	@SerializedName("tenantName")
	private String tenantName;

	@SerializedName("tenantContactNumber")
	private String tenantContactNumber;

	@SerializedName("tenantEmailAddress")
	private String tenantEmailAddress;

	@SerializedName("bookedProperyName")
	private String bookedProperyName;

	@SerializedName("previousPropertName")
	private String previousPropertName;

	@SerializedName("currentPropertName")
	private String currentPropertName;

	@SerializedName("propertAddress")
	private String propertAddress;

	@SerializedName("bedNumber")
	private String bedNumber;

	@SerializedName("expectedCheckIndate")
	private Timestamp expectedCheckIndate;

	@SerializedName("expectedCheckOutdate")
	private Timestamp expectedCheckOutdate;

	@SerializedName("checkInDate")
	private Timestamp checkInDate;

	@SerializedName("checkOutDate")
	private Timestamp checkOutDate;

	@SerializedName("checkedInDate")
	private Timestamp checkedInDate;

	@SerializedName("checkedOutDate")
	private Timestamp checkedOutDate;

	@SerializedName("suspendedDate")
	private Timestamp suspendedDate;

	@SerializedName("reasonForSuspension")
	private String reasonForSuspension;

	public Timestamp getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Timestamp checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

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

	public String getBookedProperyName() {
		return bookedProperyName;
	}

	public void setBookedProperyName(String bookedProperyName) {
		this.bookedProperyName = bookedProperyName;
	}

	public String getPreviousPropertName() {
		return previousPropertName;
	}

	public void setPreviousPropertName(String previousPropertName) {
		this.previousPropertName = previousPropertName;
	}

	public String getCurrentPropertName() {
		return currentPropertName;
	}

	public void setCurrentPropertName(String currentPropertName) {
		this.currentPropertName = currentPropertName;
	}

	public String getPropertAddress() {
		return propertAddress;
	}

	public void setPropertAddress(String propertAddress) {
		this.propertAddress = propertAddress;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public Timestamp getExpectedCheckIndate() {
		return expectedCheckIndate;
	}

	public void setExpectedCheckIndate(Timestamp expectedCheckIndate) {
		this.expectedCheckIndate = expectedCheckIndate;
	}

	public Timestamp getExpectedCheckOutdate() {
		return expectedCheckOutdate;
	}

	public void setExpectedCheckOutdate(Timestamp expectedCheckOutdate) {
		this.expectedCheckOutdate = expectedCheckOutdate;
	}

	public Timestamp getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Timestamp checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Timestamp getCheckedInDate() {
		return checkedInDate;
	}

	public void setCheckedInDate(Timestamp checkedInDate) {
		this.checkedInDate = checkedInDate;
	}

	public Timestamp getCheckedOutDate() {
		return checkedOutDate;
	}

	public void setCheckedOutDate(Timestamp checkedOutDate) {
		this.checkedOutDate = checkedOutDate;
	}

	public Timestamp getSuspendedDate() {
		return suspendedDate;
	}

	public void setSuspendedDate(Timestamp suspendedDate) {
		this.suspendedDate = suspendedDate;
	}

	public String getReasonForSuspension() {
		return reasonForSuspension;
	}

	public void setReasonForSuspension(String reasonForSuspension) {
		this.reasonForSuspension = reasonForSuspension;
	}

}
