package com.integration.zoy.entity;


import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_property_details", schema = "pgowners")
public class ZoyPgPropertyDetails {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "property_id", updatable = false, nullable = false, unique = true, length = 36)
    private String propertyId;

    @Column(name = "pg_owner_id")
    private String pgOwnerId;

    @Column(name = "pg_type_id")
    private String pgTypeId;

    @Column(name = "property_name", length = 100)
    private String propertyName;

    @Column(name = "property_pg_email", length = 100)
    private String propertyPgEmail;

    @Column(name = "property_city", length = 50)
    private String propertyCity;

    @Column(name = "property_state", length = 50)
    private String propertyState;

    @Column(name = "property_contact_number", length = 15)
    private String propertyContactNumber;

    @Column(name = "property_location_latitude", precision = 10, scale = 7)
    private BigDecimal propertyLocationLatitude;

    @Column(name = "property_location_longitude", precision = 10, scale = 7)
    private BigDecimal propertyLocationLongitude;

    @Column(name = "property_house_area")
    private String propertyHouseArea;

    @Column(name = "property_manager_name", length = 100)
    private String propertyManagerName;

    @Column(name = "property_agreement_charges")
    private BigDecimal propertyAgreementCharges;

    @Column(name = "property_charge_status")
    private Boolean propertyChargeStatus;

    @Column(name = "property_description", columnDefinition = "TEXT")
    private String propertyDescription;

    @Column(name = "property_image_name", length = 255)
    private String propertyImageName;

    @Column(name = "property_image_url", length = 255)
    private String propertyImageUrl;

    @Column(name = "property_occupancy_type", length = 50)
    private String propertyOccupancyType;

    @Column(name = "property_totalcharges")
    private BigDecimal propertyTotalCharges;

    @Column(name = "property_offline_booking_id", length = 50)
    private String propertyOfflineBookingId;

    @Column(name = "property_grace_period")
    private Integer propertyGracePeriod;

    @Column(name = "property_is_late_payment_fee")
    private Boolean propertyIsLatePaymentFee;

    @Column(name = "property_late_payment_fee")
    private BigDecimal propertyLatePaymentFee;

    @Column(name = "property_cin_numer", length = 20)
    private String propertyCinNumber;

    @Column(name = "property_gst_number", length = 20)
    private String propertyGstNumber;
    
    @Column(name = "property_locality", length = 20)
    private String propertyLocality;
    
    @Column(name = "property_pincode", length = 20)
    private String propertyPincode;
    
    @Column(name = "property_ekyc_charges")
    private BigDecimal propertyEkycCharges;
    
    @Column(name = "property_gst_status")
    private Boolean propertyGstStatus;
    
    @Column(name = "property_gst_percentage", precision = 10, scale = 7)
    private BigDecimal propertyGstPercentage;

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgOwnerId(String pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}


	public String getPgTypeId() {
		return pgTypeId;
	}

	public void setPgTypeId(String pgTypeId) {
		this.pgTypeId = pgTypeId;
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

	public BigDecimal getPropertyAgreementCharges() {
		return propertyAgreementCharges;
	}

	public void setPropertyAgreementCharges(BigDecimal propertyAgreementCharges) {
		this.propertyAgreementCharges = propertyAgreementCharges;
	}

	public Boolean getPropertyChargeStatus() {
		return propertyChargeStatus;
	}

	public void setPropertyChargeStatus(Boolean propertyChargeStatus) {
		this.propertyChargeStatus = propertyChargeStatus;
	}

	public String getPropertyDescription() {
		return propertyDescription;
	}

	public void setPropertyDescription(String propertyDescription) {
		this.propertyDescription = propertyDescription;
	}

	public String getPropertyImageName() {
		return propertyImageName;
	}

	public void setPropertyImageName(String propertyImageName) {
		this.propertyImageName = propertyImageName;
	}

	public String getPropertyImageUrl() {
		return propertyImageUrl;
	}

	public void setPropertyImageUrl(String propertyImageUrl) {
		this.propertyImageUrl = propertyImageUrl;
	}

	public String getPropertyOccupancyType() {
		return propertyOccupancyType;
	}

	public void setPropertyOccupancyType(String propertyOccupancyType) {
		this.propertyOccupancyType = propertyOccupancyType;
	}

	public BigDecimal getPropertyTotalCharges() {
		return propertyTotalCharges;
	}

	public void setPropertyTotalCharges(BigDecimal propertyTotalCharges) {
		this.propertyTotalCharges = propertyTotalCharges;
	}

	public String getPropertyOfflineBookingId() {
		return propertyOfflineBookingId;
	}

	public void setPropertyOfflineBookingId(String propertyOfflineBookingId) {
		this.propertyOfflineBookingId = propertyOfflineBookingId;
	}

	public Integer getPropertyGracePeriod() {
		return propertyGracePeriod;
	}

	public void setPropertyGracePeriod(Integer propertyGracePeriod) {
		this.propertyGracePeriod = propertyGracePeriod;
	}

	public Boolean getPropertyIsLatePaymentFee() {
		return propertyIsLatePaymentFee;
	}

	public void setPropertyIsLatePaymentFee(Boolean propertyIsLatePaymentFee) {
		this.propertyIsLatePaymentFee = propertyIsLatePaymentFee;
	}

	public BigDecimal getPropertyLatePaymentFee() {
		return propertyLatePaymentFee;
	}

	public void setPropertyLatePaymentFee(BigDecimal propertyLatePaymentFee) {
		this.propertyLatePaymentFee = propertyLatePaymentFee;
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

	public String getPropertyLocality() {
		return propertyLocality;
	}

	public void setPropertyLocality(String propertyLocality) {
		this.propertyLocality = propertyLocality;
	}

	public String getPropertyPincode() {
		return propertyPincode;
	}

	public void setPropertyPincode(String propertyPincode) {
		this.propertyPincode = propertyPincode;
	}

	public BigDecimal getPropertyEkycCharges() {
		return propertyEkycCharges;
	}

	public void setPropertyEkycCharges(BigDecimal propertyEkycCharges) {
		this.propertyEkycCharges = propertyEkycCharges;
	}

	public Boolean getPropertyGstStatus() {
		return propertyGstStatus;
	}

	public void setPropertyGstStatus(Boolean propertyGstStatus) {
		this.propertyGstStatus = propertyGstStatus;
	}

	public BigDecimal getPropertyGstPercentage() {
		return propertyGstPercentage;
	}

	public void setPropertyGstPercentage(BigDecimal propertyGstPercentage) {
		this.propertyGstPercentage = propertyGstPercentage;
	}



}

