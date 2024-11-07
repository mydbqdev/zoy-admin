package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_room_beds", schema = "pgowners")
public class ZoyPgRoomBeds {

    @EmbeddedId
    private ZoyPgRoomBedsId id;

    public ZoyPgRoomBedsId getId() {
        return id;
    }

    public void setId(ZoyPgRoomBedsId id) {
        this.id = id;
    }

 
}
