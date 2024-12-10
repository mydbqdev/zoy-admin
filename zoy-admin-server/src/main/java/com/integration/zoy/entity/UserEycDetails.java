package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_eyc_details", schema = "pgusers")
public class UserEycDetails {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_ekyc_isekycverified")
    private Boolean userEkycIsEkycVerified;

    @Column(name = "user_ekyc_masked_addhaar")
    private String userEkycMaskedAadhaar;

    @Column(name = "user_ekyc_country")
    private String userEkycCountry;

    @Column(name = "user_ekyc_careof")
    private String userEkycCareOf;

    @Column(name = "user_ekyc_pin")
    private Integer userEkycPin;

    @Column(name = "user_ekyc_vtc")
    private String userEkycVtc;

    @Column(name = "user_ekyc_street")
    private String userEkycStreet;

    @Column(name = "user_ekyc_district")
    private String userEkycDistrict;

    @Column(name = "user_ekyc_locality")
    private String userEkycLocality;

    @Column(name = "user_ekyc_state")
    private String userEkycState;

    @Column(name = "user_ekyc_landmark")
    private String userEkycLandmark;

    @Column(name = "user_ekyc_house")
    private String userEkycHouse;

    @Column(name = "user_ekyc_subdistrict")
    private String userEkycSubdistrict;

    @Column(name = "user_ekyc_postoffice")
    private String userEkycPostoffice;

    @Column(name = "user_ekyc_gender")
    private String userEkycGender;

    @Column(name = "user_ekyc_phone")
    private String userEkycPhone;

    @Column(name = "user_ekyc_generatedat")
    private Timestamp userEkycGeneratedAt;

    @Column(name = "user_ekyc_name")
    private String userEkycName;

    @Column(name = "user_ekyc_photo")
    private String userEkycPhoto;

//    @Column(name = "user_ekyc_type")
//    private Integer userEkycType;

    @Column(name = "user_ekyc_created")
    @CreationTimestamp
    private Timestamp userEkycCreated;

    @Column(name = "user_ekyc_modified")
    @UpdateTimestamp
    private Timestamp userEkycModified;

    @ManyToOne
    @JoinColumn(name = "user_ekyc_type", referencedColumnName = "user_ekyc_type_id")
    private UserEkycTypeMaster userEkycTypeMaster;
     
    public String maskAadhaar(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.length() < 4) {
            return aadhaarNumber; // Return the original number if it's too short to mask
        }
        String lastFourDigits = aadhaarNumber.substring(aadhaarNumber.length() - 4);
        String maskedPart = "********"; // Masking part
        return maskedPart + lastFourDigits; // Return masked Aadhaar
    }

    // Method to retrieve the unmasked Aadhaar number
    public String unmaskAadhaar() {
        // This assumes you have some secure way to access the actual Aadhaar.
        return userEkycMaskedAadhaar; // Return the actual Aadhaar (make sure to implement the secure retrieval)
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getUserEkycIsEkycVerified() {
        return userEkycIsEkycVerified;
    }

    public void setUserEkycIsEkycVerified(Boolean userEkycIsEkycVerified) {
        this.userEkycIsEkycVerified = userEkycIsEkycVerified;
    }

    public String getUserEkycMaskedAadhaar() {
        return userEkycMaskedAadhaar;
    }

    public void setUserEkycMaskedAadhaar(String userEkycMaskedAadhaar) {
        this.userEkycMaskedAadhaar = userEkycMaskedAadhaar;
    }

    public String getUserEkycCountry() {
        return userEkycCountry;
    }

    public void setUserEkycCountry(String userEkycCountry) {
        this.userEkycCountry = userEkycCountry;
    }

    public String getUserEkycCareOf() {
        return userEkycCareOf;
    }

    public void setUserEkycCareOf(String userEkycCareOf) {
        this.userEkycCareOf = userEkycCareOf;
    }

    public Integer getUserEkycPin() {
        return userEkycPin;
    }

    public void setUserEkycPin(Integer userEkycPin) {
        this.userEkycPin = userEkycPin;
    }

    public String getUserEkycVtc() {
        return userEkycVtc;
    }

    public void setUserEkycVtc(String userEkycVtc) {
        this.userEkycVtc = userEkycVtc;
    }

    public String getUserEkycStreet() {
        return userEkycStreet;
    }

    public void setUserEkycStreet(String userEkycStreet) {
        this.userEkycStreet = userEkycStreet;
    }

    public String getUserEkycDistrict() {
        return userEkycDistrict;
    }

    public void setUserEkycDistrict(String userEkycDistrict) {
        this.userEkycDistrict = userEkycDistrict;
    }

    public String getUserEkycLocality() {
        return userEkycLocality;
    }

    public void setUserEkycLocality(String userEkycLocality) {
        this.userEkycLocality = userEkycLocality;
    }

    public String getUserEkycState() {
        return userEkycState;
    }

    public void setUserEkycState(String userEkycState) {
        this.userEkycState = userEkycState;
    }

    public String getUserEkycLandmark() {
        return userEkycLandmark;
    }

    public void setUserEkycLandmark(String userEkycLandmark) {
        this.userEkycLandmark = userEkycLandmark;
    }

    public String getUserEkycHouse() {
        return userEkycHouse;
    }

    public void setUserEkycHouse(String userEkycHouse) {
        this.userEkycHouse = userEkycHouse;
    }

    public String getUserEkycSubdistrict() {
        return userEkycSubdistrict;
    }

    public void setUserEkycSubdistrict(String userEkycSubdistrict) {
        this.userEkycSubdistrict = userEkycSubdistrict;
    }

    public String getUserEkycPostoffice() {
        return userEkycPostoffice;
    }

    public void setUserEkycPostoffice(String userEkycPostoffice) {
        this.userEkycPostoffice = userEkycPostoffice;
    }

    public String getUserEkycGender() {
        return userEkycGender;
    }

    public void setUserEkycGender(String userEkycGender) {
        this.userEkycGender = userEkycGender;
    }

    public String getUserEkycPhone() {
        return userEkycPhone;
    }

    public void setUserEkycPhone(String userEkycPhone) {
        this.userEkycPhone = userEkycPhone;
    }

    public Timestamp getUserEkycGeneratedAt() {
        return userEkycGeneratedAt;
    }

    public void setUserEkycGeneratedAt(Timestamp userEkycGeneratedAt) {
        this.userEkycGeneratedAt = userEkycGeneratedAt;
    }

    public String getUserEkycName() {
        return userEkycName;
    }

    public void setUserEkycName(String userEkycName) {
        this.userEkycName = userEkycName;
    }

    public String getUserEkycPhoto() {
        return userEkycPhoto;
    }

    public void setUserEkycPhoto(String userEkycPhoto) {
        this.userEkycPhoto = userEkycPhoto;
    }

//    public Integer getUserEkycType() {
//        return userEkycType;
//    }
//
//    public void setUserEkycType(Integer userEkycType) {
//        this.userEkycType = userEkycType;
//    }

    public Timestamp getUserEkycCreated() {
        return userEkycCreated;
    }

    public void setUserEkycCreated(Timestamp userEkycCreated) {
        this.userEkycCreated = userEkycCreated;
    }

    public Timestamp getUserEkycModified() {
        return userEkycModified;
    }

    public void setUserEkycModified(Timestamp userEkycModified) {
        this.userEkycModified = userEkycModified;
    }
    
	public UserEkycTypeMaster getUserEkycTypeMaster() {
		return userEkycTypeMaster;
	}

	public void setUserEkycTypeMaster(UserEkycTypeMaster userEkycTypeMaster) {
		this.userEkycTypeMaster = userEkycTypeMaster;
	}

//	@Override
//	public String toString() {
//		return "UserEycDetails [userId=" + userId + ", userEkycIsEkycVerified=" + userEkycIsEkycVerified
//				+ ", userEkycMaskedAadhaar=" + userEkycMaskedAadhaar + ", userEkycCountry=" + userEkycCountry
//				+ ", userEkycCareOf=" + userEkycCareOf + ", userEkycPin=" + userEkycPin + ", userEkycVtc=" + userEkycVtc
//				+ ", userEkycStreet=" + userEkycStreet + ", userEkycDistrict=" + userEkycDistrict
//				+ ", userEkycLocality=" + userEkycLocality + ", userEkycState=" + userEkycState + ", userEkycLandmark="
//				+ userEkycLandmark + ", userEkycHouse=" + userEkycHouse + ", userEkycSubdistrict=" + userEkycSubdistrict
//				+ ", userEkycPostoffice=" + userEkycPostoffice + ", userEkycGender=" + userEkycGender
//				+ ", userEkycPhone=" + userEkycPhone + ", userEkycGeneratedAt=" + userEkycGeneratedAt
//				+ ", userEkycName=" + userEkycName + ", userEkycPhoto=" + userEkycPhoto + ", userEkycType="
//				+ userEkycType + ", userEkycCreated=" + userEkycCreated + ", userEkycModified=" + userEkycModified
//				+ "]";
//	}
//    
    
}
