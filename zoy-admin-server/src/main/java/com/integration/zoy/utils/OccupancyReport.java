package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

   
public class OccupancyReport {

   @SerializedName("Room_Number")
   String RoomNumber;

   @SerializedName("Amenities")
   String Amenities;

   @SerializedName("Share_Type")
   String ShareType;

   @SerializedName("Monthly_Rent")
   BigDecimal MonthlyRent;

   @SerializedName("Bed")
   String Bed;

   @SerializedName("Vacancy_Status")
   String VacancyStatus;

   @SerializedName("occupiedon")
   Timestamp occupiedon;

   @SerializedName("unpaidDues")
   BigDecimal unpaidDues;


    public void setRoomNumber(String RoomNumber) {
        this.RoomNumber = RoomNumber;
    }
    public String getRoomNumber() {
        return RoomNumber;
    }
    
    public void setAmenities(String Amenities) {
        this.Amenities = Amenities;
    }
    public String getAmenities() {
        return Amenities;
    }
    
    public void setShareType(String ShareType) {
        this.ShareType = ShareType;
    }
    public String getShareType() {
        return ShareType;
    }
    
    public void setMonthlyRent(BigDecimal MonthlyRent) {
        this.MonthlyRent = MonthlyRent;
    }
    public BigDecimal getMonthlyRent() {
        return MonthlyRent;
    }
    
    public void setBed(String Bed) {
        this.Bed = Bed;
    }
    public String getBed() {
        return Bed;
    }
    
    public void setVacancyStatus(String VacancyStatus) {
        this.VacancyStatus = VacancyStatus;
    }
    public String getVacancyStatus() {
        return VacancyStatus;
    }
    
    public void setOccupiedon(Timestamp occupiedon) {
        this.occupiedon = occupiedon;
    }
    public Timestamp getOccupiedon() {
        return occupiedon;
    }
    
    public void setUnpaidDues(BigDecimal unpaidDues) {
        this.unpaidDues = unpaidDues;
    }
    public BigDecimal getUnpaidDues() {
        return unpaidDues;
    }
    
}