package com.integration.zoy.utils;

import java.util.List;

public class RoomDetails {

	private String area;
	private String room_name;
	private List<String> amenities;
	private String share_type;
	private String monthly_rent;
	private String daily_rent;
	private String floor_type;
	private String remarks;
	private String room_type;

	public RoomDetails(String area, String room_name, List<String> amenities, String share_type, String monthly_rent,
			String daily_rent, String floor_type, String remarks, String room_type) {
		super();
		this.area = area;
		this.room_name = room_name;
		this.amenities = amenities;
		this.share_type = share_type;
		this.monthly_rent = monthly_rent;
		this.daily_rent = daily_rent;
		this.floor_type = floor_type;
		this.remarks = remarks;
		this.room_type = room_type;
	}

	@Override
	public String toString() {
		return "RoomDetails [area=" + area + ", room_name=" + room_name + ", amenities=" + amenities + ", share_type="
				+ share_type + ", monthly_rent=" + monthly_rent + ", daily_rent=" + daily_rent + ", floor_type="
				+ floor_type + ", remarks=" + remarks + ", room_type=" + room_type + "]";
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRoom_name() {
		return room_name;
	}

	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}

	public List<String> getAmenities() {
		return amenities;
	}

	public void setAmenities(List<String> amenities) {
		this.amenities = amenities;
	}

	public String getShare_type() {
		return share_type;
	}

	public void setShare_type(String share_type) {
		this.share_type = share_type;
	}

	public String getMonthly_rent() {
		return monthly_rent;
	}

	public void setMonthly_rent(String monthly_rent) {
		this.monthly_rent = monthly_rent;
	}

	public String getDaily_rent() {
		return daily_rent;
	}

	public void setDaily_rent(String daily_rent) {
		this.daily_rent = daily_rent;
	}

	public String getFloor_type() {
		return floor_type;
	}

	public void setFloor_type(String floor_type) {
		this.floor_type = floor_type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRoom_type() {
		return room_type;
	}

	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}

}
