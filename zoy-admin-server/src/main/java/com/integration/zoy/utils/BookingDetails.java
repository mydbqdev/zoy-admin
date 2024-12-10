package com.integration.zoy.utils;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

   
public class BookingDetails {

   @SerializedName("tenant_id")
   String tenantId;

   @SerializedName("fixedRent")
   BigDecimal fixedRent;

   @SerializedName("outDate")
   Timestamp outDate;

   @SerializedName("selectedBed")
   String selectedBed;

   @SerializedName("bed_id")
   String bedId;
   
   @SerializedName("gender")
   String gender;

   @SerializedName("inDate")
   Timestamp inDate;

   @SerializedName("gst")
   String gst;

   @SerializedName("is_terms_accepted")
   boolean isTermsAccepted;

   @SerializedName("calFixedRent")
   int calFixedRent;

   @SerializedName("securityDeposit")
   int securityDeposit;

   @SerializedName("currMonthStartDate")
   Timestamp currMonthStartDate;

   @SerializedName("room")
   String room;
   
   @SerializedName("room_id")
   String roomId;

   @SerializedName("currMonthEndDate")
   Timestamp currMonthEndDate;

   @SerializedName("noOfDays")
   int noOfDays;

   @SerializedName("phoneNumber")
   String phoneNumber;

   @SerializedName("rentCycleEndDate")
   Timestamp rentCycleEndDate;

   @SerializedName("due")
   int due;

   @SerializedName("name")
   String name;

   @SerializedName("property")
   String property;

   @SerializedName("lockInPeriod")
   String lockInPeriod;
   
   @SerializedName("rent_cycle_id")
   String rentCycleId;

   @SerializedName("share")
   String share;
   
   @SerializedName("share_id")
   String shareId;

   @SerializedName("floor")
   String floor;

   @SerializedName("floor_id")
   String floorId;
   
   @SerializedName("booking_mode")
   String bookingMode;


    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getTenantId() {
        return tenantId;
    }
    
    public void setFixedRent(BigDecimal fixedRent) {
        this.fixedRent = fixedRent;
    }
    public BigDecimal getFixedRent() {
        return fixedRent;
    }
    
    public void setOutDate(Timestamp outDate) {
        this.outDate = outDate;
    }
    public Timestamp getOutDate() {
        return outDate;
    }
    
    public void setSelectedBed(String selectedBed) {
        this.selectedBed = selectedBed;
    }
    public String getSelectedBed() {
        return selectedBed;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }
    
    public void setInDate(Timestamp inDate) {
        this.inDate = inDate;
    }
    public Timestamp getInDate() {
        return inDate;
    }
    
    public void setGst(String gst) {
        this.gst = gst;
    }
    public String getGst() {
        return gst;
    }
    
    public void setIsTermsAccepted(boolean isTermsAccepted) {
        this.isTermsAccepted = isTermsAccepted;
    }
    public boolean getIsTermsAccepted() {
        return isTermsAccepted;
    }
    
    public void setCalFixedRent(int calFixedRent) {
        this.calFixedRent = calFixedRent;
    }
    public int getCalFixedRent() {
        return calFixedRent;
    }
    
    public void setSecurityDeposit(int securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    public int getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setCurrMonthStartDate(Timestamp date) {
        this.currMonthStartDate = date;
    }
    public Timestamp getCurrMonthStartDate() {
        return currMonthStartDate;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    public String getRoom() {
        return room;
    }
    
    public void setCurrMonthEndDate(Timestamp currMonthEndDate) {
        this.currMonthEndDate = currMonthEndDate;
    }
    public Timestamp getCurrMonthEndDate() {
        return currMonthEndDate;
    }
    
    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }
    public int getNoOfDays() {
        return noOfDays;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setRentCycleEndDate(Timestamp rentCycleEndDate) {
        this.rentCycleEndDate = rentCycleEndDate;
    }
    public Timestamp getRentCycleEndDate() {
        return rentCycleEndDate;
    }
    
    public void setDue(int due) {
        this.due = due;
    }
    public int getDue() {
        return due;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setProperty(String property) {
        this.property = property;
    }
    public String getProperty() {
        return property;
    }
    
    public void setLockInPeriod(String string) {
        this.lockInPeriod = string;
    }
    public String getLockInPeriod() {
        return lockInPeriod;
    }
    
    public void setShare(String share) {
        this.share = share;
    }
    public String getShare() {
        return share;
    }
    
    public void setFloor(String floor) {
        this.floor = floor;
    }
    public String getFloor() {
        return floor;
    }
    
    public void setBookingMode(String bookingMode) {
        this.bookingMode = bookingMode;
    }
    public String getBookingMode() {
        return bookingMode;
    }
	public String getBedId() {
		return bedId;
	}
	public void setBedId(String bedId) {
		this.bedId = bedId;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String getFloorId() {
		return floorId;
	}
	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}
	public void setTermsAccepted(boolean isTermsAccepted) {
		this.isTermsAccepted = isTermsAccepted;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public String getRentCycleId() {
		return rentCycleId;
	}
	public void setRentCycleId(String rentCycleId) {
		this.rentCycleId = rentCycleId;
	}
    
    
}