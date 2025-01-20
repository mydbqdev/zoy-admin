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

	


}
