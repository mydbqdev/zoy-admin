package com.integration.zoy.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoyShortTermDetails {

	@JsonProperty("effectiveDate")
	private String effectiveDate;

	@JsonProperty("iscreate")
	private boolean iscreate;

	@JsonProperty("isApproved")
	private boolean isApproved;

	@JsonProperty("approvedBy")
	private String approvedBy;

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("delete")
	private boolean delete;

	@JsonProperty("pgType")
	private String pgType;

	@JsonProperty("ZoyShortTermDtoInfo")
	private List<ZoyShortTermDto> ZoyShortTermDtoInfo;

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


	public List<ZoyShortTermDto> getZoyShortTermDtoInfo() {
		return ZoyShortTermDtoInfo;
	}

	public void setZoyShortTermDtoInfo(List<ZoyShortTermDto> zoyShortTermDtoInfo) {
		ZoyShortTermDtoInfo = zoyShortTermDtoInfo;
	}

	
}
