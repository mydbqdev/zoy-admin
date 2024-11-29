package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_room_details", schema = "pgowners")
public class ZoyPgRoomDetails {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "room_id", updatable = false, nullable = false, unique = true, length = 36)
    private String roomId;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(name = "share_id")
    private String shareId;

    @Column(name = "room_area")
    private Double roomArea; 

    @Column(name = "room_available")
    private Boolean roomAvailable;

    @Column(name = "room_daily_rent")
    private Double roomDailyRent;

    @Column(name = "room_type_id")
    private String roomTypeId;

    @Column(name = "room_monthly_rent")
    private Double roomMonthlyRent;

    @Column(name = "room_remarks")
    private String roomRemarks;
    
    @Column(name = "room_status")
    private Boolean roomStatus;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public Double getRoomArea() {
        return roomArea;
    }

    public void setRoomArea(Double roomArea) {
        this.roomArea = roomArea;
    }

    public Boolean getRoomAvailable() {
        return roomAvailable;
    }

    public void setRoomAvailable(Boolean roomAvailable) {
        this.roomAvailable = roomAvailable;
    }
    
    public Double getRoomDailyRent() {
        return roomDailyRent;
    }

    public void setRoomDailyRent(Double roomDailyRent) {
        this.roomDailyRent = roomDailyRent;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Double getRoomMonthlyRent() {
        return roomMonthlyRent;
    }

    public void setRoomMonthlyRent(Double roomMonthlyRent) {
        this.roomMonthlyRent = roomMonthlyRent;
    }

    public String getRoomRemarks() {
        return roomRemarks;
    }

    public void setRoomRemarks(String roomRemarks) {
        this.roomRemarks = roomRemarks;
    }

	public Boolean getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(Boolean roomStatus) {
		this.roomStatus = roomStatus;
	}
}
