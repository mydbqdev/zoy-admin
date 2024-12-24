package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_auto_cancellation_period", schema = "pgcommon")
public class ZoyPgAutoCancellationPeriod {
	
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "autocancellation_id", updatable = false, nullable = false, length = 36)
    private String autoCancellationId;
    
    @Column(name = "days_To_Cancel", nullable = true)
    private Integer daysToCancel;
    
    @Column(name = "deduction_percentage", nullable = true)
    private BigDecimal deductionPercentage;

	public String getAutoCancellationId() {
		return autoCancellationId;
	}

	public void setAutoCancellationId(String autoCancellationId) {
		this.autoCancellationId = autoCancellationId;
	}

	public Integer getDaysToCancel() {
		return daysToCancel;
	}

	public void setDaysToCancel(Integer daysToCancel) {
		this.daysToCancel = daysToCancel;
	}

	public BigDecimal getDeductionPercentage() {
		return deductionPercentage;
	}

	public void setDeductionPercentage(BigDecimal deductionPercentage) {
		this.deductionPercentage = deductionPercentage;
	}
    
    

}
