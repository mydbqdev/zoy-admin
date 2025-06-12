package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_type_gender_master", schema = "pgowners")
public class PgTypeGenderMaster {

    @EmbeddedId
    private PgTypeGenderKey id;

    public PgTypeGenderMaster() {}

    public PgTypeGenderMaster(PgTypeGenderKey id) {
        this.id = id;
    }

    public PgTypeGenderKey getId() {
        return id;
    }

    public void setId(PgTypeGenderKey id) {
        this.id = id;
    }
}