package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_bed_details", schema = "pgowners")
public class ZoyPgBedDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bed_id", updatable = false, nullable = false, unique = true, length = 36)
    private String bedId;

    @Column(name = "bed_name", length = 50)
    private String bedName;

    @Column(name = "bed_available", length = 50) 
    private String bedAvailable;

    // Constructors
    public ZoyPgBedDetails() {
    }

    public ZoyPgBedDetails(String bedName, String bedAvailable) { // Updated constructor
        this.bedName = bedName;
        this.bedAvailable = bedAvailable;
    }

    // Getters and Setters
    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getBedName() {
        return bedName;
    }

    public void setBedName(String bedName) {
        this.bedName = bedName;
    }

    public String getBedAvailable() { // Getter for the new field
        return bedAvailable;
    }

    public void setBedAvailable(String bedAvailable) { // Setter for the new field
        this.bedAvailable = bedAvailable;
    }
}
