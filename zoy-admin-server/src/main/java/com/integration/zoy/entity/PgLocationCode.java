package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "pg_location_code", schema = "pgcommon")
public class PgLocationCode {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "location_code_id", nullable = false, length = 36)
	private String locationCodeId;

	@Column(name = "location_name", nullable = false, length = 255)
	private String locationName;

	@Column(name = "location_short_name", nullable = false, length = 3)
	private String locationShortName;

	public String getLocationCodeId() {
		return locationCodeId;
	}

	public void setLocationCodeId(String locationCodeId) {
		this.locationCodeId = locationCodeId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationShortName() {
		return locationShortName;
	}

	public void setLocationShortName(String locationShortName) {
		this.locationShortName = locationShortName;
	}

	@Override
	public String toString() {
		return "PgLocationCode [locationCodeId=" + locationCodeId + ", locationName=" + locationName
				+ ", locationShortName=" + locationShortName + "]";
	}


}
