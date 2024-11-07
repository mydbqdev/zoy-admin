package com.integration.zoy.utils;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;


public class PersonalDetails {

   @SerializedName("phoneNumber")
   String phoneNumber;

   @SerializedName("gender")
   String gender;

   @SerializedName("nationality")
   String nationality;

   @SerializedName("dob")
   Timestamp dob;

   @SerializedName("permanent_address")
   String permanentAddress;

   @SerializedName("email")
   String email;

   @SerializedName("tenantType")
   String tenantType;

   @SerializedName("current_address")
   String currentAddress;

   @SerializedName("altPhone")
   String altPhone;

   @SerializedName("name")
   String name;

   @SerializedName("officeorInstituteName")
   String officeorInstituteName;

   @SerializedName("blood_group")
   String bloodGroup;

   @SerializedName("officeorInstituteId")
   String officeorInstituteId;

   @SerializedName("mother_tongue")
   String motherTongue;


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }
    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public String getNationality() {
        return nationality;
    }
    
    public void setDob(Timestamp dob) {
        this.dob = dob;
    }
    public Timestamp getDob() {
        return dob;
    }
    
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }
    public String getPermanentAddress() {
        return permanentAddress;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    
    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }
    public String getTenantType() {
        return tenantType;
    }
    
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
    public String getCurrentAddress() {
        return currentAddress;
    }
    
    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
    }
    public String getAltPhone() {
        return altPhone;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setOfficeorInstituteName(String officeorInstituteName) {
        this.officeorInstituteName = officeorInstituteName;
    }
    public String getOfficeorInstituteName() {
        return officeorInstituteName;
    }
    
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    public String getBloodGroup() {
        return bloodGroup;
    }
    
    public void setOfficeorInstituteId(String officeorInstituteId) {
        this.officeorInstituteId = officeorInstituteId;
    }
    public String getOfficeorInstituteId() {
        return officeorInstituteId;
    }
    
    public void setMotherTongue(String motherTongue) {
        this.motherTongue = motherTongue;
    }
    public String getMotherTongue() {
        return motherTongue;
    }
    
}
