package com.integration.zoy.model;

import java.util.List;

public class ZoyBeforeCheckInCancellationModel {
	
	private String effectiveDate;
	
	private boolean iscreate;
	
	private boolean isApproved;
	
	private String approvedBy;

	private String createdBy;
     
	private boolean delete;
	
	private String pgType;
	
	private List<ZoyBeforeCheckInCancellation> ZoyBeforeCheckInCancellationInfo;

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
		return ZoyBeforeCheckInCancellationInfo;
	}

	public void setZoyBeforeCheckInCancellationInfo(List<ZoyBeforeCheckInCancellation> zoyBeforeCheckInCancellationInfo) {
		ZoyBeforeCheckInCancellationInfo = zoyBeforeCheckInCancellationInfo;
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
