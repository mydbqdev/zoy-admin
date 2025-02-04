package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_short_term_renting_duration", schema = "pgowners")
public class ZoyPgShortTermRentingDuration {

	@Id
	@GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "short_term_renting_duration_id", nullable = false, length = 36)
    private String rentingDurationId;

    @Column(name = "renting_duration_days", nullable = false)
    private int rentingDurationDays;

	public String getRentingDurationId() {
		return rentingDurationId;
	}

	public void setRentingDurationId(String rentingDurationId) {
		this.rentingDurationId = rentingDurationId;
	}

	public int getRentingDurationDays() {
		return rentingDurationDays;
	}

	public void setRentingDurationDays(int rentingDurationDays) {
		this.rentingDurationDays = rentingDurationDays;
	}
    
    
}
