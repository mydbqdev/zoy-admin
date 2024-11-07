package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class PropertyTotalInfo {

   @SerializedName("propertyId")
   String propertyId;

   @SerializedName("propertyName")
   String propertyName;

   @SerializedName("totalRooms")
   int totalRooms;

   @SerializedName("totalAvailableBeds")
   int totalAvailableBeds;

   @SerializedName("totalOccupiedBeds")
   int totalOccupiedBeds;

   @SerializedName("totalTenants")
   int totalTenants;

   @SerializedName("underNotice")
   int underNotice;


    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
    public String getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    public String getPropertyName() {
        return propertyName;
    }
    
    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }
    public int getTotalRooms() {
        return totalRooms;
    }
    
    public void setTotalAvailableBeds(int totalAvailableBeds) {
        this.totalAvailableBeds = totalAvailableBeds;
    }
    public int getTotalAvailableBeds() {
        return totalAvailableBeds;
    }
    
    public void setTotalOccupiedBeds(int totalOccupiedBeds) {
        this.totalOccupiedBeds = totalOccupiedBeds;
    }
    public int getTotalOccupiedBeds() {
        return totalOccupiedBeds;
    }
    
    public void setTotalTenants(int totalTenants) {
        this.totalTenants = totalTenants;
    }
    public int getTotalTenants() {
        return totalTenants;
    }
    
    public void setUnderNotice(int underNotice) {
        this.underNotice = underNotice;
    }
    public int getUnderNotice() {
        return underNotice;
    }
    
}