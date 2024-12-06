package com.integration.zoy.model;

import java.util.List;

public class Room{
	private String floorId;
	private String roomNo;
	private String numberOfBeds;
	private String bedsAvailable;
	private String bedsOccupied;
	private List<Bed> beds;
	 
	
	
	
	public String getFloorId() {
		return floorId;
	}
	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}
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
	public String getBedsOccupied() {
		return bedsOccupied;
	}
	public void setBedsOccupied(String bedsOccupied) {
		this.bedsOccupied = bedsOccupied;
	}
    
}