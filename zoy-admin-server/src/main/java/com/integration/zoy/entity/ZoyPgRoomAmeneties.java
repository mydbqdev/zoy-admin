package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.integration.zoy.repository.ZoyPgRoomAmenetiesId;

@Entity
@Table(name = "zoy_pg_room_ameneties", schema = "pgowners")
public class ZoyPgRoomAmeneties {



    @EmbeddedId
    private ZoyPgRoomAmenetiesId id;

    public ZoyPgRoomAmenetiesId getId() {
        return id;
    }

    public void setId(ZoyPgRoomAmenetiesId id) {
        this.id = id;
    }


}
