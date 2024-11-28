package com.integration.zoy.model;

import java.util.List;

public class FloorInformation {
	private String floorNumber;
	private String totalRooms;
	private String totalOccupancy;
	private String occupied;
	private String vacant;
	private List<Room> rooms;

	public String getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(String floorNumber) {
		this.floorNumber = floorNumber;
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

	@Override
	public String toString() {
		return "FloorInformation [floorNumber=" + floorNumber + ", totalRooms=" + totalRooms + ", totalOccupancy="
				+ totalOccupancy + ", occupied=" + occupied + ", vacant=" + vacant + ", rooms=" + rooms + "]";
	}

	
}