package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_properties_images", schema = "pgowners")
public class ZoyPgPropertiesImages {
 
	@Id
	@Column(name = "image_id", length = 255)
	private String imageId;

	@Column(name = "property_id", length = 255)
    private String propertyId;
 
 
 
    // Getters and Setters
    public String getPropertyId() {
        return propertyId;
    }
 
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
 
    public String getImageId() {
        return imageId;
    }
 
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    
    
 

}