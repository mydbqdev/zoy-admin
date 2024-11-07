package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_property_share_types", schema = "pgowners")
public class ZoyPgPropertyShareTypes {

    @EmbeddedId
    private ZoyPgPropertyShareTypesId id;

    public ZoyPgPropertyShareTypesId getId() {
        return id;
    }

    public void setId(ZoyPgPropertyShareTypesId id) {
        this.id = id;
    }

}
