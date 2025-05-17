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

	@JsonProperty("zoyShare")
	private BigDecimal zoyShare;

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

	@JsonProperty("property_locality")
	private String propertyLocality;

	@JsonProperty("property_house_area")
	private String propertyHouseArea;

	@JsonProperty( "property_location_latitude")
	private BigDecimal propertyLocationLatitude;

	@JsonProperty("property_location_longitude")
	private BigDecimal propertyLocationLongitude;

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

	public BigDecimal getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(BigDecimal zoyShare) {
		this.zoyShare = zoyShare;
	}

	public String getRegisterId() {
		return registerId;
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


}
