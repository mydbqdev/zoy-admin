package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_property_ameneties", schema = "pgowners")
public class ZoyPgPropertyAmeneties {

    @EmbeddedId
    private ZoyPgPropertyAmenetiesId id;

    public ZoyPgPropertyAmenetiesId getId() {
        return id;
    }

    public void setId(ZoyPgPropertyAmenetiesId id) {
        this.id = id;
    }

}
