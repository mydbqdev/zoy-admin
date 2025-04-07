package com.integration.zoy.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyBeforeCheckInCancellationModel {

	@SerializedName("effectiveDate")
	private String effectiveDate;

	@SerializedName("iscreate")
	private boolean iscreate;

	@SerializedName("isApproved")
	private boolean isApproved;

	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

	@SerializedName("delete")
	private boolean delete;

	@SerializedName("pgType")
	private String pgType;

	 @JsonProperty("ZoyBeforeCheckInCancellationInfo")
	 private List<ZoyBeforeCheckInCancellation> zoyBeforeCheckInCancellationInfo;

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIscreate() {
		return iscreate;
	}

	public void setIscreate(boolean iscreate) {
		this.iscreate = iscreate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public List<ZoyBeforeCheckInCancellation> getZoyBeforeCheckInCancellationInfo() {
	    return zoyBeforeCheckInCancellationInfo;
	}

	public void setZoyBeforeCheckInCancellationInfo(List<ZoyBeforeCheckInCancellation> info) {
	    this.zoyBeforeCheckInCancellationInfo = info;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getPgType() {
		return pgType;
	}

	public void setPgType(String pgType) {
		this.pgType = pgType;
	}

}
