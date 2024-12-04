package com.integration.zoy.model;

import java.util.List;

public class Room{
	private String roomNo;
	private String numberOfBeds;
	private String bedsAvailable;
	private List<Bed> beds;
	 
	public String getRoomNo() {
		return roomNo;
	}
	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}
	public String getNumberOfBeds() {
		return numberOfBeds;
	}
	public void setNumberOfBeds(String numberOfBeds) {
		this.numberOfBeds = numberOfBeds;
	}
	public String getBedsAvailable() {
		return bedsAvailable;
	}
	public void setBedsAvailable(String bedsAvailable) {
		this.bedsAvailable = bedsAvailable;
	}
	public List<Bed> getBeds() {
		return beds;
	}
	public void setBeds(List<Bed> beds) {
		this.beds = beds;
	}
    
}