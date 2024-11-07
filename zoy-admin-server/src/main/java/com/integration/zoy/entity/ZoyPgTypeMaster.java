package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_type_master", schema = "pgowners")
public class ZoyPgTypeMaster {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "pg_type_id", updatable = false, nullable = false, unique = true, length = 36)
    private String pgTypeId;

    @Column(name = "pg_type_name", length = 100)
    private String pgTypeName;


    public String getPgTypeId() {
        return pgTypeId;
    }

    public void setPgTypeId(String pgTypeId) {
        this.pgTypeId = pgTypeId;
    }

    public String getPgTypeName() {
        return pgTypeName;
    }

    public void setPgTypeName(String pgTypeName) {
        this.pgTypeName = pgTypeName;
    }
}
