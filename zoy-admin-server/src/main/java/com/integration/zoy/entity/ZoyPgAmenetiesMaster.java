package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_ameneties_master", schema = "pgowners")
public class ZoyPgAmenetiesMaster {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "ameneties_id", updatable = false, nullable = false, unique = true, length = 36)
	private String amenetiesId;

	@Column(name = "ameneties_name", length = 100)
	private String amenetiesName;

	@Column(name = "ameneties_image")
	private String amenetiesImage;

	// Constructors
	public ZoyPgAmenetiesMaster() {
	}

	public ZoyPgAmenetiesMaster(String amenetiesName) {
		this.amenetiesName = amenetiesName;
	}

	// Getters and Setters
	public String getAmenetiesId() {
		return amenetiesId;
	}

	public void setAmenetiesId(String amenetiesId) {
		this.amenetiesId = amenetiesId;
	}

	public String getAmenetiesName() {
		return amenetiesName;
	}

	public void setAmenetiesName(String amenetiesName) {
		this.amenetiesName = amenetiesName;
	}

	public String getAmenetiesImage() {
		return amenetiesImage;
	}

	public void setAmenetiesImage(String amenetiesImage) {
		this.amenetiesImage = amenetiesImage;
	}

}
