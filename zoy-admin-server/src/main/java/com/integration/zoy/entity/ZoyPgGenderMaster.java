package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_gender_master", schema = "pgowners")
public class ZoyPgGenderMaster {

	@Id
	@Column(name = "gender_id", length = 36, nullable = false)
	private String genderId;

	@Column(name = "gender_name", length = 50, nullable = false)
	private String genderName;

	// Default constructor
	public ZoyPgGenderMaster() {
	}

	// Parameterized constructor
	public ZoyPgGenderMaster(String genderId, String genderName) {
		this.genderId = genderId;
		this.genderName = genderName;
	}

	// Getters and setters
	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	// toString method
	@Override
	public String toString() {
		return "ZoyPgGenderMaster{" + "genderId='" + genderId + '\'' + ", genderName='" + genderName + '\'' + '}';
	}
}
