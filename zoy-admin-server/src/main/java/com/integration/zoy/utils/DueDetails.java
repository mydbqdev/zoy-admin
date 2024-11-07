package com.integration.zoy.utils;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class DueDetails {

   @SerializedName("dues")
   List<Dues> dues;

   @SerializedName("tenantName")
   String tenantName;

   @SerializedName("phoneNumber")
   String phoneNumber;

   @SerializedName("bedId")
   String bedId;

   @SerializedName("bedName")
   String bedName;
   
   @SerializedName("tenantId")
   String tenantId;

   @SerializedName("bookingId")
   String bookingId;


    public void setDues(List<Dues> dues) {
        this.dues = dues;
    }
    public List<Dues> getDues() {
        return dues;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    public String getTenantName() {
        return tenantName;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setBedId(String bedId) {
        this.bedId = bedId;
    }
    public String getBedId() {
        return bedId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getTenantId() {
        return tenantId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public String getBookingId() {
        return bookingId;
    }
	public String getBedName() {
		return bedName;
	}
	public void setBedName(String bedName) {
		this.bedName = bedName;
	}
    
    
    
}