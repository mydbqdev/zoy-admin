package com.integration.zoy.utils;
import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class OccupancyReportDetails {

   @SerializedName("propertyName")
   String propertyName;

   @SerializedName("total_rooms")
   BigDecimal totalRooms;

   @SerializedName("total_beds")
   BigDecimal totalBeds;

   @SerializedName("total_under_notice")
   BigDecimal totalUnderNotice;

   @SerializedName("total_occupied_beds")
   BigDecimal totalOccupiedBeds;

   @SerializedName("total_available_beds")
   BigDecimal totalAvailableBeds;

   @SerializedName("occupancyReport")
   List<OccupancyReport> occupancyReport;


    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    public String getPropertyName() {
        return propertyName;
    }
    
    public void setTotalRooms(BigDecimal totalRooms) {
        this.totalRooms = totalRooms;
    }
    public BigDecimal getTotalRooms() {
        return totalRooms;
    }
    
    public void setTotalBeds(BigDecimal totalBeds) {
        this.totalBeds = totalBeds;
    }
    public BigDecimal getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalUnderNotice(BigDecimal totalUnderNotice) {
        this.totalUnderNotice = totalUnderNotice;
    }
    public BigDecimal getTotalUnderNotice() {
        return totalUnderNotice;
    }
    
    public void setTotalOccupiedBeds(BigDecimal totalOccupiedBeds) {
        this.totalOccupiedBeds = totalOccupiedBeds;
    }
    public BigDecimal getTotalOccupiedBeds() {
        return totalOccupiedBeds;
    }
    
    public void setTotalAvailableBeds(BigDecimal totalAvailableBeds) {
        this.totalAvailableBeds = totalAvailableBeds;
    }
    public BigDecimal getTotalAvailableBeds() {
        return totalAvailableBeds;
    }
    
    public void setOccupancyReport(List<OccupancyReport> occupancyReport) {
        this.occupancyReport = occupancyReport;
    }
    public List<OccupancyReport> getOccupancyReport() {
        return occupancyReport;
    }
    
}