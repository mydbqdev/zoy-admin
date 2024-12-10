package com.integration.zoy.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_data_grouping", schema = "pgcommon")
public class ZoyDataGrouping {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "data_grouping_id", updatable = false, nullable = false, unique = true, length = 36)
	private String dataGroupingId;

	@Column(name = "consider_days", nullable = false, length = 36)
	private int  considerDays;

	// Getters and Setters

	public String getDataGroupingId() {
		return dataGroupingId;
	}

	public void setDataGroupingId(String dataGroupingId) {
		this.dataGroupingId = dataGroupingId;
	}

	public int getConsiderDays() {
		return considerDays;
	}

	public void setConsiderDays(int considerDays) {
		this.considerDays = considerDays;
	}

	
}
