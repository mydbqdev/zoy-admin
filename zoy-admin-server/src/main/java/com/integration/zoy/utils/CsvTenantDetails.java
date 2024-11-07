package com.integration.zoy.utils;

import java.sql.Date;
import java.sql.Timestamp;

import com.integration.zoy.service.Iso8601ToTimestampConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class CsvTenantDetails {
	@CsvBindByName(column = "firstname")
	private String firstName;

	@CsvBindByName(column = "lastname")
	private String lastName;

	@CsvBindByName(column = "phoneNumber")
	private String phoneNumber;

	@CsvBindByName(column = "alternativeNumber")
	private String alternativeNumber;

	@CsvBindByName(column = "email")
	private String email;

	@CsvBindByName(column = "dob")
	@CsvCustomBindByName(converter = Iso8601ToTimestampConverter.class, column = "dob")
	private Timestamp dob;

	@CsvBindByName(column = "gender")
	private String gender;

	@CsvBindByName(column = "permanentAddress")
	private String permanentAddress;

	@CsvBindByName(column = "nationality")
	private String nationality;

	@CsvBindByName(column = "bloodGroup")
	private String bloodGroup;

	@CsvBindByName(column = "motherTongue")
	private String motherTongue;

	@CsvBindByName(column = "tenantType")
	private String tenantType;

	@CsvBindByName(column = "govtIdNum")
	private String govtIdNum;

	@CsvBindByName(column = "vehicleNumber")
	private String vehicleNumber;

	@CsvBindByName(column = "officeName")
	private String officeName;

	@CsvBindByName(column = "officeId")
	private String officeId;

	@CsvBindByName(column = "officeAddress")
	private String officeAddress;

	@CsvBindByName(column = "officePhone")
	private String officePhone;

	@CsvBindByName(column = "extension")
	private String extension;

	@CsvBindByName(column = "floor")
	private String floor;

	// Use custom converter for ISO-8601 date format
	@CsvCustomBindByName(converter = Iso8601ToTimestampConverter.class, column = "inDate")
	private Timestamp inDate;

	@CsvBindByName(column = "lockInperiod")
	private String lockInPeriod;

	@CsvBindByName(column = "monthlyRent")
	private int monthlyRent;

	// Use custom converter for ISO-8601 date format
	@CsvCustomBindByName(converter = Iso8601ToTimestampConverter.class, column = "outDate")
	private Timestamp outDate;

	@CsvBindByName(column = "paidDeposit")
	private int paidDeposit;

	@CsvBindByName(column = "room")
	private String room;

	@CsvBindByName(column = "bed")
	private String bed;

	@CsvBindByName(column = "securityDeposit")
	private int securityDeposit;

	@CsvBindByName(column = "noticePeriod")
	private String noticePeriod;

	@CsvBindByName(column = "fixedRent")
	private String fixedRent;

	@CsvBindByName(column = "calFixedRent")
	private String calFixedRent;

	@CsvBindByName(column = "noOfDays")
	private String noOfDays;

	@CsvCustomBindByName(converter = Iso8601ToTimestampConverter.class, column = "rentCycleEndDate")
	private Timestamp rentCycleEndDate;
	// Getters and Setters

	public String getPhoneNumber() {
		return phoneNumber;
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

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAlternativeNumber() {
		return alternativeNumber;
	}

	public void setAlternativeNumber(String alternativeNumber) {
		this.alternativeNumber = alternativeNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getDob() {
		return dob;
	}

	public void setDob(Timestamp dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getMotherTongue() {
		return motherTongue;
	}

	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}

	public String getTenantType() {
		return tenantType;
	}

	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}

	public String getGovtIdNum() {
		return govtIdNum;
	}

	public void setGovtIdNum(String govtIdNum) {
		this.govtIdNum = govtIdNum;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public Timestamp getInDate() {
		return inDate;
	}

	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}

	public String getLockInPeriod() {
		return lockInPeriod;
	}

	public void setLockInPeriod(String lockInPeriod) {
		this.lockInPeriod = lockInPeriod;
	}

	public int getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(int monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public Timestamp getOutDate() {
		return outDate;
	}

	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}

	public int getPaidDeposit() {
		return paidDeposit;
	}

	public void setPaidDeposit(int paidDeposit) {
		this.paidDeposit = paidDeposit;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public int getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(int securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getFixedRent() {
		return fixedRent;
	}

	public void setFixedRent(String fixedRent) {
		this.fixedRent = fixedRent;
	}

	public String getCalFixedRent() {
		return calFixedRent;
	}

	public void setCalFixedRent(String calFixedRent) {
		this.calFixedRent = calFixedRent;
	}

	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}

	public Timestamp getRentCycleEndDate() {
		return rentCycleEndDate;
	}

	public void setRentCycleEndDate(Timestamp rentCycleEndDate) {
		this.rentCycleEndDate = rentCycleEndDate;
	}

	@Override
	public String toString() {
		return "CsvTenantDetails [firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber=" + phoneNumber
				+ ", alternativeNumber=" + alternativeNumber + ", email=" + email + ", dob=" + dob + ", gender="
				+ gender + ", permanentAddress=" + permanentAddress + ", nationality=" + nationality + ", bloodGroup="
				+ bloodGroup + ", motherTongue=" + motherTongue + ", tenantType=" + tenantType + ", govtIdNum="
				+ govtIdNum + ", vehicleNumber=" + vehicleNumber + ", officeName=" + officeName + ", officeId="
				+ officeId + ", officeAddress=" + officeAddress + ", officePhone=" + officePhone + ", extension="
				+ extension + ", floor=" + floor + ", inDate=" + inDate + ", lockInPeriod=" + lockInPeriod
				+ ", monthlyRent=" + monthlyRent + ", outDate=" + outDate + ", paidDeposit=" + paidDeposit + ", room="
				+ room + ", bed=" + bed + ", securityDeposit=" + securityDeposit + ", noticePeriod=" + noticePeriod
				+ ", fixedRent=" + fixedRent + ", calFixedRent=" + calFixedRent + ", noOfDays=" + noOfDays
				+ ", rentCycleEndDate=" + rentCycleEndDate + "]";
	}


}
