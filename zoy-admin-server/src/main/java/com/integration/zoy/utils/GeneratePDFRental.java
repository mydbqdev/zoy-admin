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


public class GeneratePDFRental   {


	@JsonProperty("pg_owner_id")
	String pgOwnerId;

	@JsonProperty("pg_name")
	String pgName;

	@JsonProperty("pg_address")
	String pgAddress;

	@JsonProperty("property_id")
	String propertyId;

	@JsonProperty("share_type")
	String shareType;

	@JsonProperty("customer_address")
	String customerAddress;

	@JsonProperty("property_amenities")
	List<String> propertyAmenities;

	@JsonProperty("room")
	String room;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("move_in_date")
	Timestamp moveInDate;
    
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("move_out_date")
	Timestamp moveOutDate;

	@JsonProperty("no_of_days")
	int noOfDays;

	@JsonProperty("booking_id")
	String bookingId;

	@JsonProperty("floor")
	String floor;

	@JsonProperty("bed")
	String bed;

	@JsonProperty("fixed_rent")
	double fixedRent;

	@JsonProperty("security_deposit")
	int securityDeposit;

	@JsonProperty("notice_period")
	int noticePeriod;

	@JsonProperty("tenant_name")
	String tenantName;

	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("time_stamp")
	Timestamp timeStamp;

	@JsonProperty("manager_name")
	String managerName;


	public void setPgOwnerId(String pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}
	public String getPgOwnerId() {
		return pgOwnerId;
	}

	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	public String getPgName() {
		return pgName;
	}

	public void setPgAddress(String pgAddress) {
		this.pgAddress = pgAddress;
	}
	public String getPgAddress() {
		return pgAddress;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getPropertyId() {
		return propertyId;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}
	public String getShareType() {
		return shareType;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setPropertyAmenities(List<String> propertyAmenities) {
		this.propertyAmenities = propertyAmenities;
	}
	public List<String> getPropertyAmenities() {
		return propertyAmenities;
	}

	public void setRoom(String room) {
		this.room = room;
	}
	public String getRoom() {
		return room;
	}

	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}
	public int getNoOfDays() {
		return noOfDays;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public String getBookingId() {
		return bookingId;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getFloor() {
		return floor;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}
	public String getBed() {
		return bed;
	}

	public void setFixedRent(double fixedRent) {
		this.fixedRent = fixedRent;
	}
	public double getFixedRent() {
		return fixedRent;
	}

	public void setSecurityDeposit(int securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public int getSecurityDeposit() {
		return securityDeposit;
	}

	public void setNoticePeriod(int noticePeriod) {
		this.noticePeriod = noticePeriod;
	}
	public int getNoticePeriod() {
		return noticePeriod;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getTenantName() {
		return tenantName;
	}


	public Timestamp getMoveInDate() {
		return moveInDate;
	}
	public void setMoveInDate(Timestamp moveInDate) {
		this.moveInDate = moveInDate;
	}
	public Timestamp getMoveOutDate() {
		return moveOutDate;
	}
	public void setMoveOutDate(Timestamp moveOutDate) {
		this.moveOutDate = moveOutDate;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerName() {
		return managerName;
	}
	@Override
	public String toString() {
		return "GeneratePDFRental [pgOwnerId=" + pgOwnerId + ", pgName=" + pgName + ", pgAddress=" + pgAddress
				+ ", propertyId=" + propertyId + ", shareType=" + shareType + ", customerAddress=" + customerAddress
				+ ", propertyAmenities=" + propertyAmenities + ", room=" + room + ", moveInDate=" + moveInDate
				+ ", moveOutDate=" + moveOutDate + ", noOfDays=" + noOfDays + ", bookingId=" + bookingId + ", floor="
				+ floor + ", bed=" + bed + ", fixedRent=" + fixedRent + ", securityDeposit=" + securityDeposit
				+ ", noticePeriod=" + noticePeriod + ", tenantName=" + tenantName + ", timeStamp=" + timeStamp
				+ ", managerName=" + managerName + "]";
	}

}
