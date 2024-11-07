package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_propety_rent_cycle", schema = "pgowners")
public class ZoyPgPropertyRentCycle {

    @EmbeddedId
    private ZoyPgPropertyRentCycleId id;

    public ZoyPgPropertyRentCycleId getId() {
        return id;
    }

    public void setId(ZoyPgPropertyRentCycleId id) {
        this.id = id;
    }

    
}
