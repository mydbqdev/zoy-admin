package com.integration.zoy.repository;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgRoomAmenetiesId implements Serializable {

	
	private static final long serialVersionUID = 1L;
	@Column(name="room_id")
    private String roomId;
	@Column(name="ameneties_id")
    private String amenetiesId;
	
	

    @Override
	public int hashCode() {
		return Objects.hash(amenetiesId, roomId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgRoomAmenetiesId other = (ZoyPgRoomAmenetiesId) obj;
		return Objects.equals(amenetiesId, other.amenetiesId) && Objects.equals(roomId, other.roomId);
	}

	public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAmenetiesId() {
        return amenetiesId;
    }

    public void setAmenetiesId(String amenetiesId) {
        this.amenetiesId = amenetiesId;
    }
}
