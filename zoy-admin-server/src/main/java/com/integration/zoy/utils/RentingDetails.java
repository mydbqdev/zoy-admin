package com.integration.zoy.utils;


import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;


public class RentingDetails {

	@SerializedName("tenant_id")
	String tenantId;

	@SerializedName("fixedRent")
	int fixedRent;

	@SerializedName("selectedBed")
	String selectedBed;

	@SerializedName("bed_id")
	String bedId;

	@SerializedName("gender")
	String gender;

	@SerializedName("gst")
	String gst;

	@SerializedName("calFixedRent")
	double calFixedRent;

	@SerializedName("securityDeposit")
	int securityDeposit;

	@SerializedName("currMonthEndDate")
	Timestamp currMonthEndDate;

	@SerializedName("noOfDays")
	String noOfDays;

	@SerializedName("gstPercent")
	String gstPercent;

	@SerializedName("includeCurrentMonth")
	boolean includeCurrentMonth;

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

	@SerializedName("outDate")
	Timestamp outDate;

	@SerializedName("inDate")
	Timestamp inDate;

	@SerializedName("is_terms_accepted")
	boolean isTermsAccepted;

	@SerializedName("currMonthStartDate")
	Timestamp currMonthStartDate;

	@SerializedName("room")
	String room;

	@SerializedName("room_id")
	String roomId;

	@SerializedName("phoneNumber")
	String phoneNumber;

	@SerializedName("rentCycleEndDate")
	Timestamp rentCycleEndDate;

	@SerializedName("due")
	int due;

	@SerializedName("name")
	String name;


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getTenantId() {
		return tenantId;
	}

	public void setFixedRent(int fixedRent) {
		this.fixedRent = fixedRent;
	}
	public int getFixedRent() {
		return fixedRent;
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

	public void setGst(String gst) {
		this.gst = gst;
	}
	public String getGst() {
		return gst;
	}

	public void setCalFixedRent(double calFixedRent) {
		this.calFixedRent = calFixedRent;
	}
	public double getCalFixedRent() {
		return calFixedRent;
	}

	public void setSecurityDeposit(int securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public int getSecurityDeposit() {
		return securityDeposit;
	}

	public void setCurrMonthEndDate(Timestamp currMonthEndDate) {
		this.currMonthEndDate = currMonthEndDate;
	}
	public Timestamp getCurrMonthEndDate() {
		return currMonthEndDate;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}
	public String getNoOfDays() {
		return noOfDays;
	}

	public void setGstPercent(String gstPercent) {
		this.gstPercent = gstPercent;
	}
	public String getGstPercent() {
		return gstPercent;
	}

	public void setIncludeCurrentMonth(boolean includeCurrentMonth) {
		this.includeCurrentMonth = includeCurrentMonth;
	}
	public boolean getIncludeCurrentMonth() {
		return includeCurrentMonth;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	public String getProperty() {
		return property;
	}

	public void setLockInPeriod(String lockInPeriod) {
		this.lockInPeriod = lockInPeriod;
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

	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}
	public Timestamp getOutDate() {
		return outDate;
	}

	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}
	public Timestamp getInDate() {
		return inDate;
	}

	public void setIsTermsAccepted(boolean isTermsAccepted) {
		this.isTermsAccepted = isTermsAccepted;
	}
	public boolean getIsTermsAccepted() {
		return isTermsAccepted;
	}

	public void setCurrMonthStartDate(Timestamp currMonthStartDate) {
		this.currMonthStartDate = currMonthStartDate;
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
	public String getBedId() {
		return bedId;
	}
	public void setBedId(String bedId) {
		this.bedId = bedId;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public String getFloorId() {
		return floorId;
	}
	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public void setTermsAccepted(boolean isTermsAccepted) {
		this.isTermsAccepted = isTermsAccepted;
	}
	public String getRentCycleId() {
		return rentCycleId;
	}
	public void setRentCycleId(String rentCycleId) {
		this.rentCycleId = rentCycleId;
	}


}