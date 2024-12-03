package com.integration.zoy.entity;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_data_grouping", schema = "pgcommon")
public class ZoyDataGrouping {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "data_grouping_id", updatable = false, nullable = false, unique = true, length = 36)
	private String dataGroupingId;

	@Column(name = "data_grouping_name", nullable = false, length = 36)
	private String dataGroupingName;

	// Getters and Setters

	public String getDataGroupingId() {
		return dataGroupingId;
	}

	public void setDataGroupingId(String dataGroupingId) {
		this.dataGroupingId = dataGroupingId;
	}

	public String getDataGroupingName() {
		return dataGroupingName;
	}

	public void setDataGroupingName(String dataGroupingName) {
		this.dataGroupingName = dataGroupingName;
	}
}
