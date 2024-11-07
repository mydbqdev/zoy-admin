package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_floor_rooms", schema = "pgowners")
public class ZoyPgFloorRooms {

    @EmbeddedId
    private ZoyPgFloorRoomsId id;

    public ZoyPgFloorRoomsId getId() {
        return id;
    }

    public void setId(ZoyPgFloorRoomsId id) {
        this.id = id;
    }

}