package com.integration.zoy.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class FollowUp {
	@JsonProperty("inquiryId")
	@SerializedName("inquiryId")
	private String inquiryId;
	
	@SerializedName("followUpDate")
	@JsonProperty("followUpDate")
	private Timestamp followUpDate;
	
	@SerializedName("remarks")
	@JsonProperty("remarks")
	private String remarks;
	
	@SerializedName("reminderSet")
	@JsonProperty("reminderSet")
	private Boolean reminderSet;
	
	@SerializedName("reminderDate")
	@JsonProperty("reminderDate")
	private Timestamp reminderDate;
	
	@SerializedName("status")
	@JsonProperty("status")
	private String status;
	public String getInquiryId() {
		return inquiryId;
	}
	public void setInquiryId(String inquiryId) {
		this.inquiryId = inquiryId;
	}
	public Timestamp getFollowUpDate() {
		return followUpDate;
	}
	public void setFollowUpDate(Timestamp followUpDate) {
		this.followUpDate = followUpDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Boolean getReminderSet() {
		return reminderSet;
	}
	public void setReminderSet(Boolean reminderSet) {
		this.reminderSet = reminderSet;
	}
	public Timestamp getReminderDate() {
		return reminderDate;
	}
	public void setReminderDate(Timestamp reminderDate) {
		this.reminderDate = reminderDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
