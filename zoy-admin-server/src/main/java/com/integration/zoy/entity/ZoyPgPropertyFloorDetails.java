package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_property_floor_details", schema = "pgowners")
public class ZoyPgPropertyFloorDetails {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "floor_id", updatable = false, nullable = false, unique = true, length = 36)
    private String floorId;
    
    @Column(name = "master_floor_id", length = 36)
    private String masterFloorId;

    @Column(name = "floor_name", length = 50)
    private String floorName;
    
    @Column(name = "floor_status")
    private Boolean floorStatus;

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

	public Boolean getFloorStatus() {
		return floorStatus;
	}

	public void setFloorStatus(Boolean floorStatus) {
		this.floorStatus = floorStatus;
	}

	public String getMasterFloorId() {
		return masterFloorId;
	}

	public void setMasterFloorId(String masterFloorId) {
		this.masterFloorId = masterFloorId;
	}
    
    
}