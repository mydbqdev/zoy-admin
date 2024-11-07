package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class LocalDetails {
	@SerializedName("altPhone")
	String altPhone;

	@SerializedName("blood_group")
	String bloodGroup;

	@SerializedName("current_address")
	String currentAddress;

	@SerializedName("dob")
	String dob;

	@SerializedName("gender")
	String gender;

	@SerializedName("mother_tongue")
	String motherTongue;

	@SerializedName("nationality")
	String nationality;

	@SerializedName("officeorInstituteId")
	String officeorInstituteId;

	@SerializedName("officeorInstituteName")
	String officeorInstituteName;

	@SerializedName("permanent_address")
	String permanentAddress;

	@SerializedName("tenantType")
	String tenantType;

	@SerializedName("ref_num")
	String refNum;

	@SerializedName("local_ofcPh")
	String localOfcPh;

	@SerializedName("local_vehicleNo")
	String localVehicleNo;

	@SerializedName("referance_name")
	String referanceName;

	@SerializedName("local_address")
	String localAddress;

	@SerializedName("local_ofcAdd")
	String localOfcAdd;


	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}
	public String getRefNum() {
		return refNum;
	}

	public void setLocalOfcPh(String localOfcPh) {
		this.localOfcPh = localOfcPh;
	}
	public String getLocalOfcPh() {
		return localOfcPh;
	}

	public void setLocalVehicleNo(String localVehicleNo) {
		this.localVehicleNo = localVehicleNo;
	}
	public String getLocalVehicleNo() {
		return localVehicleNo;
	}

	public void setReferanceName(String referanceName) {
		this.referanceName = referanceName;
	}
	public String getReferanceName() {
		return referanceName;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalOfcAdd(String localOfcAdd) {
		this.localOfcAdd = localOfcAdd;
	}
	public String getLocalOfcAdd() {
		return localOfcAdd;
	}


	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}
	public String getAltPhone() {
		return altPhone;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getDob() {
		return dob;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getGender() {
		return gender;
	}

	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}
	public String getMotherTongue() {
		return motherTongue;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getNationality() {
		return nationality;
	}

	public void setOfficeorInstituteId(String officeorInstituteId) {
		this.officeorInstituteId = officeorInstituteId;
	}
	public String getOfficeorInstituteId() {
		return officeorInstituteId;
	}

	public void setOfficeorInstituteName(String officeorInstituteName) {
		this.officeorInstituteName = officeorInstituteName;
	}
	public String getOfficeorInstituteName() {
		return officeorInstituteName;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}
	public String getTenantType() {
		return tenantType;
	}
}
