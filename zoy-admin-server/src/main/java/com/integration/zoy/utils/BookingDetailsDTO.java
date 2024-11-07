package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class BookingDetailsDTO {
	@SerializedName("bookingId")
    private String bookingId;
	@SerializedName("tenantId")
    private String tenantId;
	@SerializedName("outDate")
    private Timestamp outDate;
	@SerializedName("fixedRent")
    private String fixedRent;
	@SerializedName("selectedBed")
    private String selectedBed;
	@SerializedName("gender")
    private String gender;
	@SerializedName("inDate")
    private Timestamp inDate;
	@SerializedName("propertyName")
    private String propertyName;
	@SerializedName("propertyPgEmail")
    private String propertyPgEmail;
	@SerializedName("propertyCity")
    private String propertyCity;
	@SerializedName("propertyState")
    private String propertyState;
	@SerializedName("propertyContactNumber")
    private String propertyContactNumber;
	@SerializedName("propertyLocationLatitude")
    private String propertyLocationLatitude;
	@SerializedName("propertyLocationLongitude")
    private String propertyLocationLongitude;
	@SerializedName("propertyHouseArea")
    private String propertyHouseArea;
	@SerializedName("propertyManagerName")
    private String propertyManagerName;
	@SerializedName("propertyPincode")
    private String propertyPincode;
	@SerializedName("propertyLocality")
    private String propertyLocality;
	@SerializedName("propertyCinNumber")
    private String propertyCinNumber;
	@SerializedName("propertyGstNumber")
    private String propertyGstNumber;
	@SerializedName("pgTypeName")
    private String pgTypeName;
	@SerializedName("pgOwnerId")
    private String pgOwnerId;
	@SerializedName("pgOwnerName")
    private String pgOwnerName;
	@SerializedName("pgOwnerEmail")
    private String pgOwnerEmail;
	@SerializedName("pgOwnerMobile")
    private String pgOwnerMobile;
	@SerializedName("share")
    private String share;
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getFixedRent() {
		return fixedRent;
	}
	public void setFixedRent(String fixedRent) {
		this.fixedRent = fixedRent;
	}
	public String getSelectedBed() {
		return selectedBed;
	}
	public void setSelectedBed(String selectedBed) {
		this.selectedBed = selectedBed;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public Timestamp getOutDate() {
		return outDate;
	}
	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}
	public Timestamp getInDate() {
		return inDate;
	}
	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyPgEmail() {
		return propertyPgEmail;
	}
	public void setPropertyPgEmail(String propertyPgEmail) {
		this.propertyPgEmail = propertyPgEmail;
	}
	public String getPropertyCity() {
		return propertyCity;
	}
	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}
	public String getPropertyState() {
		return propertyState;
	}
	public void setPropertyState(String propertyState) {
		this.propertyState = propertyState;
	}
	public String getPropertyContactNumber() {
		return propertyContactNumber;
	}
	public void setPropertyContactNumber(String propertyContactNumber) {
		this.propertyContactNumber = propertyContactNumber;
	}
	public String getPropertyLocationLatitude() {
		return propertyLocationLatitude;
	}
	public void setPropertyLocationLatitude(String propertyLocationLatitude) {
		this.propertyLocationLatitude = propertyLocationLatitude;
	}
	public String getPropertyLocationLongitude() {
		return propertyLocationLongitude;
	}
	public void setPropertyLocationLongitude(String propertyLocationLongitude) {
		this.propertyLocationLongitude = propertyLocationLongitude;
	}
	public String getPropertyHouseArea() {
		return propertyHouseArea;
	}
	public void setPropertyHouseArea(String propertyHouseArea) {
		this.propertyHouseArea = propertyHouseArea;
	}
	public String getPropertyManagerName() {
		return propertyManagerName;
	}
	public void setPropertyManagerName(String propertyManagerName) {
		this.propertyManagerName = propertyManagerName;
	}
	public String getPropertyPincode() {
		return propertyPincode;
	}
	public void setPropertyPincode(String propertyPincode) {
		this.propertyPincode = propertyPincode;
	}
	public String getPropertyLocality() {
		return propertyLocality;
	}
	public void setPropertyLocality(String propertyLocality) {
		this.propertyLocality = propertyLocality;
	}
	public String getPropertyCinNumber() {
		return propertyCinNumber;
	}
	public void setPropertyCinNumber(String propertyCinNumber) {
		this.propertyCinNumber = propertyCinNumber;
	}
	public String getPropertyGstNumber() {
		return propertyGstNumber;
	}
	public void setPropertyGstNumber(String propertyGstNumber) {
		this.propertyGstNumber = propertyGstNumber;
	}
	public String getPgTypeName() {
		return pgTypeName;
	}
	public void setPgTypeName(String pgTypeName) {
		this.pgTypeName = pgTypeName;
	}
	public String getPgOwnerId() {
		return pgOwnerId;
	}
	public void setPgOwnerId(String pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}
	public String getPgOwnerName() {
		return pgOwnerName;
	}
	public void setPgOwnerName(String pgOwnerName) {
		this.pgOwnerName = pgOwnerName;
	}
	public String getPgOwnerEmail() {
		return pgOwnerEmail;
	}
	public void setPgOwnerEmail(String pgOwnerEmail) {
		this.pgOwnerEmail = pgOwnerEmail;
	}
	public String getPgOwnerMobile() {
		return pgOwnerMobile;
	}
	public void setPgOwnerMobile(String pgOwnerMobile) {
		this.pgOwnerMobile = pgOwnerMobile;
	}
	public String getShare() {
		return share;
	}
	public void setShare(String share) {
		this.share = share;
	}
	@Override
	public String toString() {
		return "BookingDetailsDTO [bookingId=" + bookingId + ", tenantId=" + tenantId + ", outDate=" + outDate
				+ ", fixedRent=" + fixedRent + ", selectedBed=" + selectedBed + ", gender=" + gender + ", inDate="
				+ inDate + ", propertyName=" + propertyName + ", propertyPgEmail=" + propertyPgEmail + ", propertyCity="
				+ propertyCity + ", propertyState=" + propertyState + ", propertyContactNumber=" + propertyContactNumber
				+ ", propertyLocationLatitude=" + propertyLocationLatitude + ", propertyLocationLongitude="
				+ propertyLocationLongitude + ", propertyHouseArea=" + propertyHouseArea + ", propertyManagerName="
				+ propertyManagerName + ", propertyPincode=" + propertyPincode + ", propertyLocality="
				+ propertyLocality + ", propertyCinNumber=" + propertyCinNumber + ", propertyGstNumber="
				+ propertyGstNumber + ", pgTypeName=" + pgTypeName + ", pgOwnerId=" + pgOwnerId + ", pgOwnerName="
				+ pgOwnerName + ", pgOwnerEmail=" + pgOwnerEmail + ", pgOwnerMobile=" + pgOwnerMobile + ", share="
				+ share + "]";
	}
	
	
}

