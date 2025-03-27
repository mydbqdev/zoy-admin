package com.integration.zoy.entity;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "zoy_pg_rent_gst", schema = "pgowners")
public class ZoyPgGstCharges {

    @Id
	@GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "rent_id", nullable = false, length = 36)
    private String rentId;

    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "component_name", nullable = false, length = 50)
    private String componentName;
    
    @Column(name = "igst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal igstPercentage;
    
    @Column(name = "cgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal cgstPercentage;
    
    @Column(name = "sgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal sgstPercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_Approved")
	private Boolean isApproved;

	@Column(name = "effective_Date")
	private String effectiveDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="approved_by")
	private String approvedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public BigDecimal getIgstPercentage() {
		return igstPercentage;
	}

	public void setIgstPercentage(BigDecimal igstPercentage) {
		this.igstPercentage = igstPercentage;
	}

	public BigDecimal getCgstPercentage() {
		return cgstPercentage;
	}

	public void setCgstPercentage(BigDecimal cgstPercentage) {
		this.cgstPercentage = cgstPercentage;
	}

	public BigDecimal getSgstPercentage() {
		return sgstPercentage;
	}

	public void setSgstPercentage(BigDecimal sgstPercentage) {
		this.sgstPercentage = sgstPercentage;
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
    
}
