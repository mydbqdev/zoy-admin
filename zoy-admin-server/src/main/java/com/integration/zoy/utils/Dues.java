package com.integration.zoy.utils;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class Dues {

   @SerializedName("images")
   List<String> images;

   @SerializedName("bill_start_date")
   Date billStartDate;

   @SerializedName("bill_timestamp")
   Date billTimestamp;

   @SerializedName("due_id")
   String dueId;

   @SerializedName("due_amount")
   BigDecimal dueAmount;

   @SerializedName("billing_type")
   String billingType;
   
   @SerializedName("billing_type_id")
   String billingTypeId;

   @SerializedName("description")
   String description;

   @SerializedName("due_type")
   String dueType;
   
   @SerializedName("due_type_id")
   String dueTypeId;

   @SerializedName("bill_end_date")
   Date billEndDate;


    public void setImages(List<String> images) {
        this.images = images;
    }
    public List<String> getImages() {
        return images;
    }
    
    public void setBillStartDate(Date billStartDate) {
        this.billStartDate = billStartDate;
    }
    public Date getBillStartDate() {
        return billStartDate;
    }
    
    public void setBillTimestamp(Date billTimestamp) {
        this.billTimestamp = billTimestamp;
    }
    public Date getBillTimestamp() {
        return billTimestamp;
    }
    
    public void setDueId(String dueId) {
        this.dueId = dueId;
    }
    public String getDueId() {
        return dueId;
    }
    
    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }
    public BigDecimal getDueAmount() {
        return dueAmount;
    }
    
    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }
    public String getBillingType() {
        return billingType;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setDueType(String dueType) {
        this.dueType = dueType;
    }
    public String getDueType() {
        return dueType;
    }
    
    public void setBillEndDate(Date billEndDate) {
        this.billEndDate = billEndDate;
    }
    public Date getBillEndDate() {
        return billEndDate;
    }
	public String getBillingTypeId() {
		return billingTypeId;
	}
	public void setBillingTypeId(String billingTypeId) {
		this.billingTypeId = billingTypeId;
	}
	public String getDueTypeId() {
		return dueTypeId;
	}
	public void setDueTypeId(String dueTypeId) {
		this.dueTypeId = dueTypeId;
	}
    
    
}