package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_type_gender_master", schema = "pgowners")
public class PgTypeGenderMapping {

	@Id
	@Column(name = "pg_type_id", nullable = false, length = 36)
	private String pgTypeId;

	@Column(name = "gender_id", nullable = false, length = 36)
	private String genderId;

	public String getPgTypeId() {
		return pgTypeId;
	}

	public void setPgTypeId(String pgTypeId) {
		this.pgTypeId = pgTypeId;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

}
