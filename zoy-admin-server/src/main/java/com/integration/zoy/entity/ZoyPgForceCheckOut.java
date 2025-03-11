package com.integration.zoy.entity;

import javax.persistence.*;


@Entity
@Table(name = "zoy_pg_force_check_out", schema = "pgowners")
public class ZoyPgForceCheckOut {

    @Id
    @Column(name = "force_check_out_id", nullable = false, length = 36)
    private String forceCheckOutId;

    @Column(name = "force_check_out_days", nullable = false)
    private int forceCheckOutDays;

    @Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="approved_by")
	private String approvedBy;
	
    // Getters and Setters
    public String getForceCheckOutId() {
        return forceCheckOutId;
    }

    public void setForceCheckOutId(String forceCheckOutId) {
        this.forceCheckOutId = forceCheckOutId;
    }

    public int getForceCheckOutDays() {
        return forceCheckOutDays;
    }

    public void setForceCheckOutDays(int forceCheckOutDays) {
        this.forceCheckOutDays = forceCheckOutDays;
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
	
	@Override
	public String toString() {
		return "ZoyPgForceCheckOut [forceCheckOutId=" + forceCheckOutId + ", forceCheckOutDays=" + forceCheckOutDays
				+ ", isApproved=" + isApproved + ", effectiveDate=" + effectiveDate + ", createdBy=" + createdBy
				+ ", approvedBy=" + approvedBy + "]";
	}
}
