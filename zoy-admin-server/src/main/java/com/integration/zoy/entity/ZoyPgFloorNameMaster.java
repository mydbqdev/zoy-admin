package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_floor_name_master", schema = "pgowners")
public class ZoyPgFloorNameMaster {
	 @Id
	    @GeneratedValue(generator = "UUID")
	   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	   	@Column(name = "floor_name_id", updatable = false, nullable = false, unique = true, length = 36)
	    private String foorNameId;

	    @Column(name = "floor_name", length = 100)
	    private String floorName;

		public String getFoorNameId() {
			return foorNameId;
		}

		public void setFoorNameId(String foorNameId) {
			this.foorNameId = foorNameId;
		}

		public String getFloorName() {
			return floorName;
		}

		public void setFloorName(String floorName) {
			this.floorName = floorName;
		}
	    
	    
}
