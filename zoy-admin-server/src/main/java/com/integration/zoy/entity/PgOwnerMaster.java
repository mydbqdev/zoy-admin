package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "pg_owner_master", schema = "pgadmin")
public class PgOwnerMaster {

	@Id
	@Column(name = "zoy_code", nullable = false, length = 36)
	private String zoyCode;

	@Column(name = "first_name", nullable = false, length = 36)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 36)
	private String lastName;

	@Column(name = "email_id", nullable = false, length = 36)
	private String emailId;

	@Column(name = "mobile_no", nullable = false)
	private String mobileNo;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private Timestamp updated_at;

	@Column(name = "zoy_share", nullable = false)
	private BigDecimal zoyShare;

	@Column(name ="register_id")
	private String registerId;

	@Column(name ="property_name")
	private String propertyName;

	@Column(name ="property_pincode")
	private Integer propertyPincode;

	@Column(name ="property_state")
	private String propertyState;

	@Column(name ="property_city")
	private String propertyCity;

	@Column(name ="property_locality")
	private String propertyLocality;

	@Column(name ="property_house_area")
	private String propertyHouseArea;

	@Column(name = "property_location_latitude", precision = 10, scale = 7)
	private BigDecimal propertyLocationLatitude;

	@Column(name = "property_location_longitude", precision = 10, scale = 7)
	private BigDecimal propertyLocationLongitude;


	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getZoyCode() {
		return zoyCode;
	}

	public void setZoyCode(String zoyCode) {
		this.zoyCode = zoyCode;
	}

	public Timestamp getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}

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

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public BigDecimal getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(BigDecimal zoyShare) {
		this.zoyShare = zoyShare;
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



}
