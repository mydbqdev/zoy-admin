package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class UserDuesResponse {
    private List<String> images;  // Assuming this is a List of image names
    private Timestamp billStartDate;
    private Timestamp billTimestamp;
    private String due_id;
    private BigDecimal dueAmount;
    private String billingType;
    private String description;
    private String dueType;
    private Timestamp billEndDate;
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public Timestamp getBillStartDate() {
		return billStartDate;
	}
	public void setBillStartDate(Timestamp billStartDate) {
		this.billStartDate = billStartDate;
	}
	public Timestamp getBillTimestamp() {
		return billTimestamp;
	}
	public void setBillTimestamp(Timestamp billTimestamp) {
		this.billTimestamp = billTimestamp;
	}
	public String getDue_id() {
		return due_id;
	}
	public void setDue_id(String due_id) {
		this.due_id = due_id;
	}
	public BigDecimal getDueAmount() {
		return dueAmount;
	}
	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}
	public String getBillingType() {
		return billingType;
	}
	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDueType() {
		return dueType;
	}
	public void setDueType(String dueType) {
		this.dueType = dueType;
	}
	public Timestamp getBillEndDate() {
		return billEndDate;
	}
	public void setBillEndDate(Timestamp billEndDate) {
		this.billEndDate = billEndDate;
	}

    
}
