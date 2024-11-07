package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgFloorRoomsId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="floor_id")
    private String floorId;
	@Column(name="room_id")
    private String roomId;

    @Override
	public int hashCode() {
		return Objects.hash(floorId, roomId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgFloorRoomsId other = (ZoyPgFloorRoomsId) obj;
		return floorId == other.floorId && roomId == other.roomId;
	}

	public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

   

}
