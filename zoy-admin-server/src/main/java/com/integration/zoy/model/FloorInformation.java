package com.integration.zoy.model;

import java.util.ArrayList;
import java.util.List;

public class FloorInformation {
	private String floorName ="";
	private String floorId ="";
	private String totalRooms = "0";
	private String totalOccupancy = "0";
	private String occupied ="0";
	private String vacant ="0";
	private List<Room> rooms = new ArrayList<>();

	

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public String getTotalRooms() {
		return totalRooms;
	}

	public void setTotalRooms(String totalRooms) {
		this.totalRooms = totalRooms;
	}

	public String getTotalOccupancy() {
		return totalOccupancy;
	}

	public void setTotalOccupancy(String totalOccupancy) {
		this.totalOccupancy = totalOccupancy;
	}

	public String getOccupied() {
		return occupied;
	}

	public void setOccupied(String occupied) {
		this.occupied = occupied;
	}

	public String getVacant() {
		return vacant;
	}

	public void setVacant(String vacant) {
		this.vacant = vacant;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	
	public String getFloorId() {
		return floorId;
	}

	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}

	@Override
	public String toString() {
		return "FloorInformation [floorName=" + floorName + ", floorId=" + floorId + ", totalRooms=" + totalRooms
				+ ", totalOccupancy=" + totalOccupancy + ", occupied=" + occupied + ", vacant=" + vacant + ", rooms="
				+ rooms + "]";
	}

	

	
}