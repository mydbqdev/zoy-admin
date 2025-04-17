package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_auto_cancellation_after_check_in", schema = "pgowners")
public class ZoyPgAutoCancellationAfterCheckIn {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "auto_cancellation_id", updatable = false, nullable = false, unique = true, length = 36)
    private String autoCancellationId;

    @Column(name = "trigger_on", length = 100)
    private String triggerOn;

    @Column(name = "trigger_condition", length = 10)
    private String triggerCondition;

    @Column(name = "auto_cancellation_day", nullable = false)
    private Long autoCancellationDay;

    @Column(name = "deduction_percentage", precision = 10, scale = 2)
    private BigDecimal deductionPercentage;

    @Column(name = "cond", length = 100)
    private String cond;

    @Column(name = "trigger_value", length = 100)
    private String triggerValue;
    
    @Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="approved_by")
	private String approvedBy;
	
	@Column(name="comments")
	private String comments;

    // Getters and Setters

    public String getAutoCancellationId() {
        return autoCancellationId;
    }

    public void setAutoCancellationId(String autoCancellationId) {
        this.autoCancellationId = autoCancellationId;
    }

    public String getTriggerOn() {
        return triggerOn;
    }

    public void setTriggerOn(String triggerOn) {
        this.triggerOn = triggerOn;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public Long getAutoCancellationDay() {
        return autoCancellationDay;
    }

    public void setAutoCancellationDay(Long autoCancellationDay) {
        this.autoCancellationDay = autoCancellationDay;
    }

    public BigDecimal getDeductionPercentage() {
        return deductionPercentage;
    }

    public void setDeductionPercentage(BigDecimal deductionPercentage) {
        this.deductionPercentage = deductionPercentage;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
    
}
