package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "zoy_pg_short_term_master", schema = "pgowners")
public class ZoyPgShortTermMaster {

	@Id
	@GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "zoy_pg_short_term_master_id", updatable = false, nullable = false, unique = true, length = 36)
	private String zoyPgShortTermMasterId;

	@Column(name = "start_day", nullable = false)
	private int startDay;

	@Column(name = "end_day", nullable = false)
	private int endDay;

	@Column(name = "percentage", nullable = false, precision = 2)
	private BigDecimal percentage;

	// Getters and Setters
	public String getZoyPgShortTermMasterId() {
		return zoyPgShortTermMasterId;
	}

	public void setZoyPgShortTermMasterId(String zoyPgShortTermMasterId) {
		this.zoyPgShortTermMasterId = zoyPgShortTermMasterId;
	}

	public int getStartDay() {
		return startDay;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
}
