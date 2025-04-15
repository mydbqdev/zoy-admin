package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TenantList {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private Timestamp dateOfBirth;
	private String gender;
	private String permanentAddress;
	private Timestamp inDate;
	private Timestamp outDate;
	private String floor;
	private String room;
	private String bedNumber;
	private BigDecimal depositPaid;
	private String rentPaid;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Timestamp getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Timestamp dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
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
	public Timestamp getInDate() {
		return inDate;
	}
	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}
	public Timestamp getOutDate() {
		return outDate;
	}
	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getBedNumber() {
		return bedNumber;
	}
	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}
	public BigDecimal getDepositPaid() {
		return depositPaid;
	}
	public void setDepositPaid(BigDecimal depositPaid) {
		this.depositPaid = depositPaid;
	}
	public String getRentPaid() {
		return rentPaid;
	}
	public void setRentPaid(String rentPaid) {
		this.rentPaid = rentPaid;
	}

	@Override
	public String toString() {
		return "TenantList [firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber=" + phoneNumber
				+ ", email=" + email + ", dateOfBirth=" + dateOfBirth + ", gender=" + gender + ", permanentAddress="
				+ permanentAddress + ", inDate=" + inDate + ", outDate=" + outDate + ", floor=" + floor + ", room="
				+ room + ", bedNumber=" + bedNumber + ", depositPaid=" + depositPaid + ", rentPaid=" + rentPaid + "]";
	}
}
