package com.integration.zoy.utils;
import java.sql.Timestamp;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class BookingInfo {

   @SerializedName("booking_id")
   String bookingId;

   @SerializedName("booking_date")
   Timestamp bookingDate;

   @SerializedName("web_check_in")
   boolean webCheckIn;

   @SerializedName("pg_owner_id")
   String pgOwnerId;

   @SerializedName("property_id")
   String propertyId;

   @SerializedName("web_check_out")
   boolean webCheckOut;

   @SerializedName("is_cancelled")
   boolean isCancelled;


    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }
    public Timestamp getBookingDate() {
        return bookingDate;
    }
    
    public void setWebCheckIn(boolean webCheckIn) {
        this.webCheckIn = webCheckIn;
    }
    public boolean getWebCheckIn() {
        return webCheckIn;
    }
    
    public void setPgOwnerId(String pgOwnerId) {
        this.pgOwnerId = pgOwnerId;
    }
    public String getPgOwnerId() {
        return pgOwnerId;
    }
    
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
    public String getPropertyId() {
        return propertyId;
    }
    
    public void setWebCheckOut(boolean webCheckOut) {
        this.webCheckOut = webCheckOut;
    }
    public boolean getWebCheckOut() {
        return webCheckOut;
    }
    
    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    public boolean getIsCancelled() {
        return isCancelled;
    }
    
}