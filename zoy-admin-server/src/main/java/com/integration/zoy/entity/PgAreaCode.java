package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "pg_area_code", schema = "pgcommon")
public class PgAreaCode {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "area_code_id", nullable = false, length = 36)
	private String areaCodeId;

	@Column(name = "area_name", nullable = false, length = 255)
	private String areaName;

	@Column(name = "area_short_name", nullable = false, length = 3)
	private String areaShortName;

	public String getAreaCodeId() {
		return areaCodeId;
	}

	public void setAreaCodeId(String areaCodeId) {
		this.areaCodeId = areaCodeId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaShortName() {
		return areaShortName;
	}

	public void setAreaShortName(String areaShortName) {
		this.areaShortName = areaShortName;
	}

	@Override
	public String toString() {
		return "PgAreaCode [areaCodeId=" + areaCodeId + ", areaName=" + areaName + ", areaShortName=" + areaShortName
				+ "]";
	}

	

}
