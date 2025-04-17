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
