package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgOwnerDetails {

	@JsonProperty("zoyCode")
	private String zoyCode;

	@JsonProperty("ownerName")
	private String ownerName;

	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("mobileNo")
	private String mobileNo;

	@JsonProperty("createdDate")
	private Timestamp createdDate;

	@JsonProperty("status")
	private String status;

	@JsonProperty("zoyShare")
	private BigDecimal zoyShare;
	
	@JsonProperty("pgName")
	private String pgName;

	@JsonProperty("registerId")
	private String registerId;

	public String getZoyCode() {
		return zoyCode;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(BigDecimal zoyShare) {
		this.zoyShare = zoyShare;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getPgName() {
		return pgName;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	
	

}
