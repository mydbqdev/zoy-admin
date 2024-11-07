package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_property_dues", schema = "pgowners")
public class ZoyPgPropertyDues {

    @EmbeddedId
    private ZoyPgPropertyDuesId id;

    public ZoyPgPropertyDuesId getId() {
        return id;
    }

    public void setId(ZoyPgPropertyDuesId id) {
        this.id = id;
    }

}