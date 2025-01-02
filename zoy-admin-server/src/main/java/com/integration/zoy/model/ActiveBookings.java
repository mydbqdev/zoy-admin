package com.integration.zoy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

public class ActiveBookings {
	@SerializedName("pgName")
	private String pgName;
	
	@SerializedName("monthlyRent")
	private BigDecimal monthlyRent;
	
	@SerializedName("securityDeposit")
    private BigDecimal securityDeposit;
	
	@SerializedName("totalDueAmount")
    private BigDecimal totalDueAmount;
	
	@SerializedName("roomBedName")
    private String roomBedName;
	
	@SerializedName("checkInDate")
    private Timestamp checkInDate;
	
	@SerializedName("checkOutDate")
    private Timestamp checkOutDate;
	
	@SerializedName("rentCycle")
    private String rentCycle;
	
	@SerializedName("noticePeriod")
    private String noticePeriod;
	public String getPgName() {
		return pgName;
	}
	public void setPgName(String pgName) {
		this.pgName = pgName;
	}
	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}
	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}
	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public BigDecimal getTotalDueAmount() {
		return totalDueAmount;
	}
	public void setTotalDueAmount(BigDecimal totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}
	public String getRoomBedName() {
		return roomBedName;
	}
	public void setRoomBedName(String roomBedName) {
		this.roomBedName = roomBedName;
	}
	public Timestamp getCheckInDate() {
		return checkInDate;
	}
	public void setCheckInDate(Timestamp checkInDate) {
		this.checkInDate = checkInDate;
	}
	public Timestamp getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(Timestamp checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	public String getRentCycle() {
		return rentCycle;
	}
	public void setRentCycle(String rentCycle) {
		this.rentCycle = rentCycle;
	}
	public String getNoticePeriod() {
		return noticePeriod;
	}
	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}
   
    
}
