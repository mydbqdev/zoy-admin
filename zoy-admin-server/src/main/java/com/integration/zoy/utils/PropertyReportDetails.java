package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

   
public class PropertyReportDetails {

   @SerializedName("property_name")
   String propertyName;

   @SerializedName("totalNooftenants")
   BigDecimal totalNooftenants;

   @SerializedName("totalCountOfRooms")
   BigDecimal totalCountOfRooms;

   @SerializedName("total_bookings")
   BigDecimal totalBookings;

   @SerializedName("totalOtherBillData")
   BigDecimal totalOtherBillData;

   @SerializedName("totalPaidCollectionData")
   BigDecimal totalPaidCollectionData;

   @SerializedName("totalsecurityDepositData")
   BigDecimal totalsecurityDepositData;

   @SerializedName("totalFoodBills")
   BigDecimal totalFoodBills;


    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    public String getPropertyName() {
        return propertyName;
    }
    
    public void setTotalNooftenants(BigDecimal totalNooftenants) {
        this.totalNooftenants = totalNooftenants;
    }
    public BigDecimal getTotalNooftenants() {
        return totalNooftenants;
    }
    
    public void setTotalCountOfRooms(BigDecimal totalCountOfRooms) {
        this.totalCountOfRooms = totalCountOfRooms;
    }
    public BigDecimal getTotalCountOfRooms() {
        return totalCountOfRooms;
    }
    
    public void setTotalBookings(BigDecimal totalBookings) {
        this.totalBookings = totalBookings;
    }
    public BigDecimal getTotalBookings() {
        return totalBookings;
    }
    
    public void setTotalOtherBillData(BigDecimal totalOtherBillData) {
        this.totalOtherBillData = totalOtherBillData;
    }
    public BigDecimal getTotalOtherBillData() {
        return totalOtherBillData;
    }
    
    public void setTotalPaidCollectionData(BigDecimal totalPaidCollectionData) {
        this.totalPaidCollectionData = totalPaidCollectionData;
    }
    public BigDecimal getTotalPaidCollectionData() {
        return totalPaidCollectionData;
    }
    
    public void setTotalsecurityDepositData(BigDecimal totalsecurityDepositData) {
        this.totalsecurityDepositData = totalsecurityDepositData;
    }
    public BigDecimal getTotalsecurityDepositData() {
        return totalsecurityDepositData;
    }
    
    public void setTotalFoodBills(BigDecimal totalFoodBills) {
        this.totalFoodBills = totalFoodBills;
    }
    public BigDecimal getTotalFoodBills() {
        return totalFoodBills;
    }
    
}