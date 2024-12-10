package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_due_factor_master", schema = "pgowners")
public class ZoyPgDueFactorMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "factor_id", updatable = false, nullable = false, unique = true, length = 36)
    private String factorId;

    @Column(name = "factor_name", length = 100)
    private String factorName;

    // Constructors
    public ZoyPgDueFactorMaster() {
    }

    public ZoyPgDueFactorMaster(String factorName) {
        this.factorName = factorName;
    }

    // Getters and Setters
    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

}