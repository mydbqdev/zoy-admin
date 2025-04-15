package com.integration.zoy.utils;

import java.util.List;

public class PropertyList {
	private String floorName;
	private String roomName;
	private String roomType;
	private String shareType;
	private Double area;
	private List<String> availableBeds;
	private Double monthlyRent;
	private List<String> amenities;
	private String remarks;
	public String getFloorName() {
		return floorName;
	}
	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public String getShareType() {
		return shareType;
	}
	public void setShareType(String shareType) {
		this.shareType = shareType;
	}
	public Double getArea() {
		return area;
	}
	public void setArea(Double area) {
		this.area = area;
	}
	public List<String> getAvailableBeds() {
		return availableBeds;
	}
	public void setAvailableBeds(List<String> availableBeds) {
		this.availableBeds = availableBeds;
	}
	public Double getMonthlyRent() {
		return monthlyRent;
	}
	public void setMonthlyRent(Double monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
	public List<String> getAmenities() {
		return amenities;
	}
	public void setAmenities(List<String> amenities) {
		this.amenities = amenities;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "PropertyList [floorName=" + floorName + ", roomName=" + roomName + ", roomType=" + roomType
				+ ", shareType=" + shareType + ", area=" + area + ", availableBeds=" + availableBeds + ", monthlyRent="
				+ monthlyRent + ", amenities=" + amenities + ", remarks=" + remarks + "]";
	}


}
