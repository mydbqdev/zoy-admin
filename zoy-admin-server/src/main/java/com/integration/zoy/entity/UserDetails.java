package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_details", schema = "pgusers")
public class UserDetails {

    @Id
//    @GeneratedValue(generator = "UUID")
//	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
//	@Column(name = "user_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "user_family_father_name")
    private String familyFatherName;

    @Column(name = "user_family_father_num")
    private String familyFatherNum;

    @Column(name = "user_local_ofcph")
    private String localOfcph;

    @Column(name = "user_reference_name")
    private String referenceName;

    @Column(name = "user_local_address")
    private String localAddress;

    @Column(name = "user_local_ofcadd")
    private String localOfcAdd;

    @Column(name = "user_local_vehicleno")
    private String localVehicleNo;

    @Column(name = "user_local_ref_num")
    private String localRefNum;

    @Column(name = "user_personal_phone_num")
    private String personalPhoneNum;

    @Column(name = "user_personal_dob")
    private Timestamp personalDob;

    @Column(name = "user_personal_permanant_address", columnDefinition = "TEXT")
    private String personalPermanentAddress;

    @Column(name = "user_personal_email")
    private String personalEmail;

    @Column(name = "user_personal_tenant_type")
    private String personalTenantType;

    @Column(name = "user_personal_alt_phone")
    private String personalAltPhone;

    @Column(name = "user_personal_name")
    private String personalName;

    @Column(name = "user_personal_curr_address")
    private String personalCurrAddress;

    @Column(name = "user_personal_off_inst_name")
    private String personalOffInstName;

    @Column(name = "user_personal_blood_group")
    private String personalBloodGroup;

    @Column(name = "user_personal_off_inst_id")
    private String personalOffInstId;

    @Column(name = "user_personal_mother_tounge")
    private String personalMotherTounge;

    @Column(name = "user_personal_gender")
    private String personalGender;

    @Column(name = "user_personal_nationality")
    private String personalNationality;

    @Column(name = "user_profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(name = "user_profile_created_at")
    @CreationTimestamp
    private Timestamp profileCreatedAt;

    @Column(name = "user_profile_modified_at")
    @UpdateTimestamp
    private Timestamp profileModifiedAt;
    
    @Column(name = "user_gst_number")
    private String userGstNumber;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFamilyFatherName() {
        return familyFatherName;
    }

    public void setFamilyFatherName(String familyFatherName) {
        this.familyFatherName = familyFatherName;
    }

    public String getFamilyFatherNum() {
        return familyFatherNum;
    }

    public void setFamilyFatherNum(String familyFatherNum) {
        this.familyFatherNum = familyFatherNum;
    }

    public String getLocalOfcph() {
        return localOfcph;
    }

    public void setLocalOfcph(String localOfcph) {
        this.localOfcph = localOfcph;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getLocalOfcAdd() {
        return localOfcAdd;
    }

    public void setLocalOfcAdd(String localOfcAdd) {
        this.localOfcAdd = localOfcAdd;
    }

    public String getLocalVehicleNo() {
        return localVehicleNo;
    }

    public void setLocalVehicleNo(String localVehicleNo) {
        this.localVehicleNo = localVehicleNo;
    }

    public String getLocalRefNum() {
        return localRefNum;
    }

    public void setLocalRefNum(String localRefNum) {
        this.localRefNum = localRefNum;
    }

    public String getPersonalPhoneNum() {
        return personalPhoneNum;
    }

    public void setPersonalPhoneNum(String personalPhoneNum) {
        this.personalPhoneNum = personalPhoneNum;
    }

    public Timestamp getPersonalDob() {
        return personalDob;
    }

    public void setPersonalDob(Timestamp personalDob) {
        this.personalDob = personalDob;
    }

    public String getPersonalPermanentAddress() {
        return personalPermanentAddress;
    }

    public void setPersonalPermanentAddress(String personalPermanentAddress) {
        this.personalPermanentAddress = personalPermanentAddress;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPersonalTenantType() {
        return personalTenantType;
    }

    public void setPersonalTenantType(String personalTenantType) {
        this.personalTenantType = personalTenantType;
    }

    public String getPersonalAltPhone() {
        return personalAltPhone;
    }

    public void setPersonalAltPhone(String personalAltPhone) {
        this.personalAltPhone = personalAltPhone;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalCurrAddress() {
        return personalCurrAddress;
    }

    public void setPersonalCurrAddress(String personalCurrAddress) {
        this.personalCurrAddress = personalCurrAddress;
    }

    public String getPersonalOffInstName() {
        return personalOffInstName;
    }

    public void setPersonalOffInstName(String personalOffInstName) {
        this.personalOffInstName = personalOffInstName;
    }

    public String getPersonalBloodGroup() {
        return personalBloodGroup;
    }

    public void setPersonalBloodGroup(String personalBloodGroup) {
        this.personalBloodGroup = personalBloodGroup;
    }

    public String getPersonalOffInstId() {
        return personalOffInstId;
    }

    public void setPersonalOffInstId(String personalOffInstId) {
        this.personalOffInstId = personalOffInstId;
    }

    public String getPersonalMotherTounge() {
        return personalMotherTounge;
    }

    public void setPersonalMotherTounge(String personalMotherTounge) {
        this.personalMotherTounge = personalMotherTounge;
    }

    public String getPersonalGender() {
        return personalGender;
    }

    public void setPersonalGender(String personalGender) {
        this.personalGender = personalGender;
    }

    public String getPersonalNationality() {
        return personalNationality;
    }

    public void setPersonalNationality(String personalNationality) {
        this.personalNationality = personalNationality;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Timestamp getProfileCreatedAt() {
        return profileCreatedAt;
    }

    public void setProfileCreatedAt(Timestamp profileCreatedAt) {
        this.profileCreatedAt = profileCreatedAt;
    }

    public Timestamp getProfileModifiedAt() {
        return profileModifiedAt;
    }

    public void setProfileModifiedAt(Timestamp profileModifiedAt) {
        this.profileModifiedAt = profileModifiedAt;
    }

	public String getUserGstNumber() {
		return userGstNumber;
	}

	public void setUserGstNumber(String userGstNumber) {
		this.userGstNumber = userGstNumber;
	}
    
    
}
