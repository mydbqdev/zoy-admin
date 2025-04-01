package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyOtherChargesDto {

	@SerializedName("otherChargesId")
	private String otherChargesId;

	@SerializedName("ownerDocumentCharges")
	private BigDecimal ownerDocumentCharges;
	
	@SerializedName("ownerEkycCharges")
	private BigDecimal ownerEkycCharges;

	@SerializedName("tenantDocumentCharges")
	private BigDecimal tenantDocumentCharges;
	
	@SerializedName("tenantEkycCharges")
	private BigDecimal tenantEkycCharges;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("isApproved")
	private boolean isApproved;
	
	@SerializedName("approvedBy")
	private String approvedBy;

	@SerializedName("createdBy")
	private String createdBy;

		
	public String getOtherChargesId() {
		return otherChargesId;
	}

	public void setOtherChargesId(String otherChargesId) {
		this.otherChargesId = otherChargesId;
	}

	public BigDecimal getOwnerDocumentCharges() {
		return ownerDocumentCharges;
	}

	public void setOwnerDocumentCharges(BigDecimal ownerDocumentCharges) {
		this.ownerDocumentCharges = ownerDocumentCharges;
	}

	public BigDecimal getTenantDocumentCharges() {
		return tenantDocumentCharges;
	}

	public void setTenantDocumentCharges(BigDecimal tenantDocumentCharges) {
		this.tenantDocumentCharges = tenantDocumentCharges;
	}

	public BigDecimal getOwnerEkycCharges() {
		return ownerEkycCharges;
	}

	public void setOwnerEkycCharges(BigDecimal ownerEkycCharges) {
		this.ownerEkycCharges = ownerEkycCharges;
	}

	public BigDecimal getTenantEkycCharges() {
		return tenantEkycCharges;
	}

	public void setTenantEkycCharges(BigDecimal tenantEkycCharges) {
		this.tenantEkycCharges = tenantEkycCharges;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
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

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	


}
