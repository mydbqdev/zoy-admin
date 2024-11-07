package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgRoomBedsId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="room_id")
    private String roomId;
	@Column(name="bed_id")
    private String bedId;

    @Override
	public int hashCode() {
		return Objects.hash(bedId, roomId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgRoomBedsId other = (ZoyPgRoomBedsId) obj;
		return Objects.equals(bedId, other.bedId) && Objects.equals(roomId, other.roomId);
	}

	public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

}
