package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_cancellation_details", schema = "pgowners")
public class ZoyPgCancellationDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cancellation_id", nullable = false, length = 36)
    private String cancellationId;

    @Column(name = "before_checkin_days")
    private Integer daysBeforeCheckIn;
    
    @Column(name = "deduction_percentage", precision = 10, scale = 2)
    private BigDecimal deductionPercentages;

    

    // Getters and Setters
    public String getCancellationId() {
        return cancellationId;
    }

    public void setCancellationId(String cancellationId) {
        this.cancellationId = cancellationId;
    }

	public Integer getDaysBeforeCheckIn() {
		return daysBeforeCheckIn;
	}

	public void setDaysBeforeCheckIn(Integer daysBeforeCheckIn) {
		this.daysBeforeCheckIn = daysBeforeCheckIn;
	}

	public BigDecimal getDeductionPercentages() {
		return deductionPercentages;
	}

	public void setDeductionPercentages(BigDecimal deductionPercentages) {
		this.deductionPercentages = deductionPercentages;
	}

	@Override
	public String toString() {
		return "ZoyPgCancellationDetails [cancellationId=" + cancellationId + ", daysBeforeCheckIn=" + daysBeforeCheckIn
				+ ", deductionPercentages=" + deductionPercentages + "]";
	}

  
}