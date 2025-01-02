package com.integration.zoy.model;

import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class TenantProfile {
	@SerializedName("tenantName")
	private String tenantName;
	
	@SerializedName("contactNumber")
    private String contactNumber;
	
	@SerializedName("userEmail")
    private String userEmail;
	
	@SerializedName("status")
    private String status;
	
	@SerializedName("ekycStatus")
    private String ekycStatus;
	
	@SerializedName("registeredDate")
    private Timestamp registeredDate;
	
	@SerializedName("currentPropertyName")
    private String currentPropertyName;
	
	@SerializedName("emergencyContactNumber")
    private String emergencyContactNumber;
	
	@SerializedName("alternatePhone")
    private String alternatePhone;
	
	@SerializedName("tenantType")
    private String tenantType;
	
	@SerializedName("gender")
    private String gender;
	
	@SerializedName("dateOfBirth")
    private Timestamp dateOfBirth;
	
	@SerializedName("bloodGroup")
    private String bloodGroup;
	
	@SerializedName("fatherName")
    private String fatherName;
	
	@SerializedName("currentAddress")
    private String currentAddress;
	
	@SerializedName("permanentAddress")
    private String permanentAddress;
	
	@SerializedName("nationality")
    private String nationality;
	
	@SerializedName("motherTongue")
    private String motherTongue;
	
	@SerializedName("userProfile")
    private String userProfile;
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEkycStatus() {
		return ekycStatus;
	}
	public void setEkycStatus(String ekycStatus) {
		this.ekycStatus = ekycStatus;
	}
	public Timestamp getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}
	public String getCurrentPropertyName() {
		return currentPropertyName;
	}
	public void setCurrentPropertyName(String currentPropertyName) {
		this.currentPropertyName = currentPropertyName;
	}
	public String getEmergencyContactNumber() {
		return emergencyContactNumber;
	}
	public void setEmergencyContactNumber(String emergencyContactNumber) {
		this.emergencyContactNumber = emergencyContactNumber;
	}
	public String getAlternatePhone() {
		return alternatePhone;
	}
	public void setAlternatePhone(String alternatePhone) {
		this.alternatePhone = alternatePhone;
	}
	public String getTenantType() {
		return tenantType;
	}
	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Timestamp getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Timestamp dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getMotherTongue() {
		return motherTongue;
	}
	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}
	public String getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}
    
}
