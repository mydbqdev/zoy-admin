package com.integration.zoy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class ZoyPgPropertyFloorsId implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "floor_id")
    private String floorId;
 
    @Column(name = "property_id")
    private String propertyId;
    
    // Getters and Setters
    public String getFloorId() {
        return floorId;
    }
 
    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
 
	public String getPropertyId() {
		return propertyId;
	}
 
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
 
}
