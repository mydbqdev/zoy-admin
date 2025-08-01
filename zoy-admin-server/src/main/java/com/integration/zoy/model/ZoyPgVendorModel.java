package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class ZoyPgVendorModel {

	@SerializedName("vendorId")
	private String vendorId;

	@SerializedName("userDesignation")
	private String userDesignation;

	@SerializedName("userGroupName")
	private String userGroupName;

	@SerializedName("rejectedReason")
	private String rejectedReason;

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

	@Override
	public String toString() {
		return "ZoyPgVendorModel [vendorId=" + vendorId + ", userDesignation=" + userDesignation + ", userGroupName="
				+ userGroupName + ", rejectedReason=" + rejectedReason + "]";
	}


}
