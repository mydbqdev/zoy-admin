package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.integration.zoy.service.Iso8601ToTimestampConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

public class CsvTenantDetails {
    
	@CsvBindByPosition(position = 0)
    private String firstName;

    @CsvBindByPosition(position = 1)
    private String lastName;

    @CsvBindByPosition(position = 2)
    private String phoneNumber;

    @CsvBindByPosition(position = 3)
    private String email;

    @CsvCustomBindByPosition(position = 4, converter = Iso8601ToTimestampConverter.class)
    private Timestamp dob;

    @CsvBindByPosition(position = 5)
    private String gender;

    @CsvBindByPosition(position = 6)
    private String permanentAddress;

    @CsvCustomBindByPosition(position = 7, converter = Iso8601ToTimestampConverter.class)
    private Timestamp outDate;

    @CsvBindByPosition(position = 8)
    private String floor;

    @CsvBindByPosition(position = 9)
    private String room;

    @CsvBindByPosition(position = 10)
    private String bedNumber;

//    @CsvBindByPosition(position = 11)
//    private String rentCycle;
//
//    @CsvBindByPosition(position = 12)
//    private BigDecimal depositPaid;

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

//    public String getRentCycle() {
//        return rentCycle;
//    }
//
//    public void setRentCycle(String rentCycle) {
//        this.rentCycle = rentCycle;
//    }
//
//    public BigDecimal getDepositPaid() {
//        return depositPaid;
//    }
//
//    public void setDepositPaid(BigDecimal depositPaid) {
//        this.depositPaid = depositPaid;
//    }

    @Override
    public String toString() {
        return "CsvTenantDetails [firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber=" + phoneNumber
                + ", email=" + email + ", dob=" + dob + ", gender=" + gender + ", permanentAddress=" + permanentAddress
                + ", outDate=" + outDate + ", floor=" + floor + ", room=" + room + ", bedNumber=" + bedNumber + "]";
    }
}