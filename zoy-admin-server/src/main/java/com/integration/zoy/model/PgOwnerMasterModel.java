package com.integration.zoy.model;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgOwnerMasterModel {


	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("mobileNo")
	private String mobileNo;

	//	@JsonProperty("zoyShare")
	//	private BigDecimal zoyShare;
	@JsonProperty("zoy_variable_share")
	private BigDecimal zoyVariableShare;

	@JsonProperty("zoy_fixed_share")
	private BigDecimal zoyFixedShare;

	@JsonProperty("registerId")
	private String registerId;

	@JsonProperty("property_name")
	private String propertyName;

	@JsonProperty("property_pincode")
	private Integer propertyPincode;

	@JsonProperty("property_state")
	private String propertyState;

	@JsonProperty("property_state_short_name")
	private String propertyStateShortName;

	@JsonProperty("property_city")
	private String propertyCity;

	@JsonProperty("property_city_code_id")
	private String propertyCityCodeId;

	@JsonProperty("property_city_code")
	private String propertyCityCode;

	@JsonProperty("property_locality")
	private String propertyLocality;

	@JsonProperty("property_locality_code_id")
	private String propertyLocalityCodeId;

	@JsonProperty("property_locality_code")
	private String propertyLocalityCode;

	@JsonProperty("property_house_area")
	private String propertyHouseArea;

	@JsonProperty( "property_location_latitude")
	private BigDecimal propertyLocationLatitude;

	@JsonProperty("property_location_longitude")
	private BigDecimal propertyLocationLongitude;

	@JsonProperty("property_door_number")
	private String propertyDoorNumber;

	@JsonProperty("property_street_name")
	private String propertyStreetName;
	
	@JsonProperty("intial_zoy_code")
	private Boolean intialZoyCode;
	
	@JsonProperty("zoy_code")
	private String zoyCode;

	@JsonProperty("ticket_status")
	private String ticketStatus;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

//	public BigDecimal getZoyShare() {
//		return zoyShare;
//	}
//
//	public void setZoyShare(BigDecimal zoyShare) {
//		this.zoyShare = zoyShare;
//	}
	
	public String getRegisterId() {
		return registerId;
	}

	public BigDecimal getZoyVariableShare() {
		return zoyVariableShare;
	}

	public void setZoyVariableShare(BigDecimal zoyVariableShare) {
		this.zoyVariableShare = zoyVariableShare;
	}

	public BigDecimal getZoyFixedShare() {
		return zoyFixedShare;
	}

	public void setZoyFixedShare(BigDecimal zoyFixedShare) {
		this.zoyFixedShare = zoyFixedShare;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Integer getPropertyPincode() {
		return propertyPincode;
	}

	public void setPropertyPincode(Integer propertyPincode) {
		this.propertyPincode = propertyPincode;
	}

	public String getPropertyState() {
		return propertyState;
	}

	public void setPropertyState(String propertyState) {
		this.propertyState = propertyState;
	}

	public String getPropertyCity() {
		return propertyCity;
	}

	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}

	public String getPropertyLocality() {
		return propertyLocality;
	}

	public void setPropertyLocality(String propertyLocality) {
		this.propertyLocality = propertyLocality;
	}

	public String getPropertyHouseArea() {
		return propertyHouseArea;
	}

	public void setPropertyHouseArea(String propertyHouseArea) {
		this.propertyHouseArea = propertyHouseArea;
	}

	public BigDecimal getPropertyLocationLatitude() {
		return propertyLocationLatitude;
	}

	public void setPropertyLocationLatitude(BigDecimal propertyLocationLatitude) {
		this.propertyLocationLatitude = propertyLocationLatitude;
	}

	public BigDecimal getPropertyLocationLongitude() {
		return propertyLocationLongitude;
	}

	public void setPropertyLocationLongitude(BigDecimal propertyLocationLongitude) {
		this.propertyLocationLongitude = propertyLocationLongitude;
	}

	public String getPropertyStateShortName() {
		return propertyStateShortName;
	}

	public void setPropertyStateShortName(String propertyStateShortName) {
		this.propertyStateShortName = propertyStateShortName;
	}

	public String getPropertyCityCode() {
		return propertyCityCode;
	}

	public void setPropertyCityCode(String propertyCityCode) {
		this.propertyCityCode = propertyCityCode;
	}

	public String getPropertyLocalityCode() {
		return propertyLocalityCode;
	}

	public void setPropertyLocalityCode(String propertyLocalityCode) {
		this.propertyLocalityCode = propertyLocalityCode;
	}

	public String getPropertyCityCodeId() {
		return propertyCityCodeId;
	}

	public void setPropertyCityCodeId(String propertyCityCodeId) {
		this.propertyCityCodeId = propertyCityCodeId;
	}

	public String getPropertyLocalityCodeId() {
		return propertyLocalityCodeId;
	}

	public void setPropertyLocalityCodeId(String propertyLocalityCodeId) {
		this.propertyLocalityCodeId = propertyLocalityCodeId;
	}

	public String getPropertyDoorNumber() {
		return propertyDoorNumber;
	}

	public void setPropertyDoorNumber(String propertyDoorNumber) {
		this.propertyDoorNumber = propertyDoorNumber;
	}

	public String getPropertyStreetName() {
		return propertyStreetName;
	}

	public void setPropertyStreetName(String propertyStreetName) {
		this.propertyStreetName = propertyStreetName;
	}

	public Boolean getIntialZoyCode() {
		return intialZoyCode;
	}

	public void setIntialZoyCode(Boolean intialZoyCode) {
		this.intialZoyCode = intialZoyCode;
	}

	public String getZoyCode() {
		return zoyCode;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}


}
