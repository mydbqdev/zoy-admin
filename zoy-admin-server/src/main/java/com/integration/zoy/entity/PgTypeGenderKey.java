package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PgTypeGenderKey implements Serializable {

    @Column(name = "pg_type_id", nullable = false, length = 36)
    private String pgTypeId;

    @Column(name = "gender_id", nullable = false, length = 36)
    private String genderId;

    public PgTypeGenderKey() {}

    public PgTypeGenderKey(String pgTypeId, String genderId) {
        this.pgTypeId = pgTypeId;
        this.genderId = genderId;
    }

    public String getPgTypeId() {
        return pgTypeId;
    }

    public void setPgTypeId(String pgTypeId) {
        this.pgTypeId = pgTypeId;
    }

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PgTypeGenderKey)) return false;
        PgTypeGenderKey that = (PgTypeGenderKey) o;
        return Objects.equals(pgTypeId, that.pgTypeId) &&
               Objects.equals(genderId, that.genderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pgTypeId, genderId);
    }
}