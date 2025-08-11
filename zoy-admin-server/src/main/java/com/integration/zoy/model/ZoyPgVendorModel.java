package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class ZoyPgVendorModel {

	@SerializedName("vendorId")
	private String vendorId;

	@SerializedName("userDesignation")
	private String userDesignation;

	@SerializedName("userDesignationName")
	private String userDesignationName;

	
	@SerializedName("userGroupName")
	private String userGroupName;

	@SerializedName("rejectedReason")
	private String rejectedReason;
	
	@SerializedName("reason")
	private String reason;
	
	@SerializedName("status")
	private String status;

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getUserDesignation() {
		return userDesignation;
	}

	public void setUserDesignation(String userDesignation) {
		this.userDesignation = userDesignation;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserDesignationName() {
		return userDesignationName;
	}

	public void setUserDesignationName(String userDesignationName) {
		this.userDesignationName = userDesignationName;
	}

	@Override
	public String toString() {
		return "ZoyPgVendorModel [vendorId=" + vendorId + ", userDesignation=" + userDesignation
				+ ", userDesignationName=" + userDesignationName + ", userGroupName=" + userGroupName
				+ ", rejectedReason=" + rejectedReason + ", reason=" + reason + ", status=" + status + "]";
	}

	


}
