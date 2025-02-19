package com.integration.zoy.model;

import java.sql.Timestamp;

public class TenantResportsDTO {

	private String tenantName;
	private String tenantContactNumber;
	private String tenantEmailAddress;
	private String bookedProperyName;
	private String previousPropertName;
	private String currentPropertName;
	private String propertAddress;
	private String roomNumber;
	private Timestamp expectedCheckIndate;
	private Timestamp expectedCheckOutdate;
	private Timestamp checkInDate;
	private Timestamp checkedInDate;
	private Timestamp checkedOutDate;
	private Timestamp suspendedDate;
	private String reasonForSuspension;

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

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
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
