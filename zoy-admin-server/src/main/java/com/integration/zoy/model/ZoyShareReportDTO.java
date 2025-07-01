package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.google.gson.annotations.SerializedName;

public class ZoyShareReportDTO {

	@SerializedName("transactionDate")
	private Timestamp transactionDate;

	@SerializedName("invoiceNumber")
	private String invoiceNumber;

	@SerializedName("pgName")
	private String pgName;

	@SerializedName("tenantName")
	private String tenantName;

	@SerializedName("sharingType")
	private String sharingType;

	@SerializedName("bedNumber")
	private String bedNumber;

	@SerializedName("paymentMode")
	private String paymentMode;

	@SerializedName("amountPaid")
	private BigDecimal amountPaid;

	@SerializedName("zoyShare")
	private String zoyShare;

	@SerializedName("zoyShareAmount")
	private BigDecimal zoyShareAmount;

	// Getters and Setters

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getSharingType() {
		return sharingType;
	}

	public void setSharingType(String sharingType) {
		this.sharingType = sharingType;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getBedNumber() {
		return bedNumber;
	}

	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(String zoyShare) {
		this.zoyShare = zoyShare;
	}

	public BigDecimal getZoyShareAmount() {
		return zoyShareAmount;
	}

	public void setZoyShareAmount(BigDecimal zoyShareAmount) {
		this.zoyShareAmount = zoyShareAmount;
	}
}
