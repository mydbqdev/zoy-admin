package com.integration.zoy.entity;
 
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
 
@Entity
@Table(name = "zoy_pg_property_floors", schema = "pgowners")
public class ZoyPgPropertyFloors {
 
	  public ZoyPgPropertyFloorsId getId() {
		return id;
	}

	public void setId(ZoyPgPropertyFloorsId id) {
		this.id = id;
	}

	@EmbeddedId
	    private ZoyPgPropertyFloorsId id;
    
}