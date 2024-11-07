package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class BedDetails {

   @SerializedName("available_beds")
   List<Beds> availableBeds;

   @SerializedName("occupied_beds")
   List<Beds> occupiedBeds;

   @SerializedName("monthly_rent")
   int monthlyRent;

   @SerializedName("share_type")
   String shareType;

   @SerializedName("share_id")
   String shareId;
   
    public void setAvailableBeds(List<Beds> availableBeds) {
        this.availableBeds = availableBeds;
    }
    public List<Beds> getAvailableBeds() {
        return availableBeds;
    }
    
    public void setOccupiedBeds(List<Beds> occupiedBeds) {
        this.occupiedBeds = occupiedBeds;
    }
    public List<Beds> getOccupiedBeds() {
        return occupiedBeds;
    }
    
    public void setMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    public int getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setShareType(String shareType) {
        this.shareType = shareType;
    }
    public String getShareType() {
        return shareType;
    }
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
    
}