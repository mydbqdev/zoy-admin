package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_cancellation_details", schema = "pgowners")
public class ZoyPgCancellationDetails {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "cancellation_id", nullable = false, length = 36)
	private String cancellationId;

	@Column(name = "priority")
	private Integer priority;

	@Column(name = "trigger_on", length = 100)
	private String triggerOn;

	@Column(name = "trigger_condition", length = 10)
	private String triggerCondition;

	@Column(name = "before_checkin_days")
	private Integer beforeCheckinDays;

	@Column(name = "deduction_percentage", precision = 10, scale = 2)
	private BigDecimal deductionPercentage;

	@Column(name = "cond", length = 100)
	private String cond;

	@Column(name = "trigger_value", length = 100)
	private String triggerValue;

	@Column(name = "create_at")
	private Timestamp createAt;
	
	@Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="approved_by")
	private String approvedBy;

	@Column(name="pg_type")
	private String pgType;
	
	public String getCancellationId() {
		return cancellationId;
	}

	public void setCancellationId(String cancellationId) {
		this.cancellationId = cancellationId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public Integer getBeforeCheckinDays() {
		return beforeCheckinDays;
	}

	public void setBeforeCheckinDays(Integer beforeCheckinDays) {
		this.beforeCheckinDays = beforeCheckinDays;
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

	public Timestamp getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
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

	public String getPgType() {
		return pgType;
	}

	public void setPgType(String pgType) {
		this.pgType = pgType;
	}

	
	@Override
	public String toString() {
		return "ZoyPgCancellationDetails [cancellationId=" + cancellationId + ", priority=" + priority + ", triggerOn="
				+ triggerOn + ", triggerCondition=" + triggerCondition + ", beforeCheckinDays=" + beforeCheckinDays
				+ ", deductionPercentage=" + deductionPercentage + ", cond=" + cond + ", triggerValue=" + triggerValue
				+ ", createAt=" + createAt + ", isApproved=" + isApproved + ", effectiveDate=" + effectiveDate
				+ ", createdBy=" + createdBy + ", approvedBy=" + approvedBy + ", pgType=" + pgType + "]";
	}


}