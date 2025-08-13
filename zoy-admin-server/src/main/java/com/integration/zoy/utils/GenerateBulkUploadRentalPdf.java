package com.integration.zoy.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

/**
 * GeneratePDFRental
 */
@Validated


public class GenerateBulkUploadRentalPdf   {


	@JsonProperty("pgName")
	String pgName;

	@JsonProperty("tenantName")
	String tenantName;

	@JsonProperty("tenantAddress")
	String tenantAddress;

	@JsonProperty("ownerName")
	String ownerName;

	@JsonProperty("ownerPhNo")
	String ownerPhNo;

	@JsonProperty("ownerAddress")
	String ownerAddress;

	@JsonProperty("pgAddress")
	String pgAddress;

	@JsonProperty("pgCity")
	String pgCity;

	@JsonProperty("shareName")
	String shareName;

	@JsonProperty("floorName")
	String floorName;

	@JsonProperty("roomName")
	String roomName;

	@JsonProperty("securityWord")
	String securityWord;
	
	@JsonProperty("securityAmt")
	String securityAmt;

	@JsonProperty("rentWord")
	String rentWord;
	
	@JsonProperty("rentAmt")
	String rentAmt;

	@JsonProperty("inDate")
	String inDate;

	@JsonProperty("outDate")
	String outDate;

	@JsonProperty("modeOfPayment")
	String modeOfPayment;

	@JsonProperty("rentCycle")
	String rentCycle;

	@JsonProperty("noOfDays")
	String noOfDays;

	@JsonProperty("officeAddress")
	String officeAddress;
	
	@JsonProperty("isRental")
	Boolean isRental;
	
	@JsonProperty("bookingId")
	String bookingId;


	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantAddress(String tenantAddress) {
		this.tenantAddress = tenantAddress;
	}
	public String getTenantAddress() {
		return tenantAddress;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerPhNo(String ownerPhNo) {
		this.ownerPhNo = ownerPhNo;
	}
	public String getOwnerPhNo() {
		return ownerPhNo;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setPgAddress(String pgAddress) {
		this.pgAddress = pgAddress;
	}
	public String getPgAddress() {
		return pgAddress;
	}

	public void setPgCity(String pgCity) {
		this.pgCity = pgCity;
	}
	public String getPgCity() {
		return pgCity;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}
	public String getShareName() {
		return shareName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getFloorName() {
		return floorName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomName() {
		return roomName;
	}

	public void setSecurityWord(String securityWord) {
		this.securityWord = securityWord;
	}
	public String getSecurityWord() {
		return securityWord;
	}

	public void setRentWord(String rentWord) {
		this.rentWord = rentWord;
	}
	public String getRentWord() {
		return rentWord;
	}

	public void setInDate(String inDate) {
		this.inDate = inDate;
	}
	public String getInDate() {
		return inDate;
	}

	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	public String getOutDate() {
		return outDate;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setRentCycle(String rentCycle) {
		this.rentCycle = rentCycle;
	}
	public String getRentCycle() {
		return rentCycle;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}
	public String getNoOfDays() {
		return noOfDays;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}
	public String getOfficeAddress() {
		return officeAddress;
	}
	public String getPgName() {
		return pgName;
	}
	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	public Boolean getIsRental() {
		return isRental;
	}
	public void setIsRental(Boolean isRental) {
		this.isRental = isRental;
	}
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public String getSecurityAmt() {
		return securityAmt;
	}
	public void setSecurityAmt(String securityAmt) {
		this.securityAmt = securityAmt;
	}
	public String getRentAmt() {
		return rentAmt;
	}
	public void setRentAmt(String rentAmt) {
		this.rentAmt = rentAmt;
	}
	@Override
	public String toString() {
		return "GenerateBulkUploadRentalPdf [pgName=" + pgName + ", tenantName=" + tenantName + ", tenantAddress="
				+ tenantAddress + ", ownerName=" + ownerName + ", ownerPhNo=" + ownerPhNo + ", ownerAddress="
				+ ownerAddress + ", pgAddress=" + pgAddress + ", pgCity=" + pgCity + ", shareName=" + shareName
				+ ", floorName=" + floorName + ", roomName=" + roomName + ", securityWord=" + securityWord
				+ ", securityAmt=" + securityAmt + ", rentWord=" + rentWord + ", rentAmt=" + rentAmt + ", inDate="
				+ inDate + ", outDate=" + outDate + ", modeOfPayment=" + modeOfPayment + ", rentCycle=" + rentCycle
				+ ", noOfDays=" + noOfDays + ", officeAddress=" + officeAddress + ", isRental=" + isRental
				+ ", bookingId=" + bookingId + "]";
	}
	
	



}

