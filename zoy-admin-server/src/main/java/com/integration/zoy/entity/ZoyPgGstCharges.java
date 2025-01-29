package com.integration.zoy.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "zoy_pg_rent_gst", schema = "pgowners")
public class ZoyPgGstCharges {

    @Id
    @Column(name = "rent_id", nullable = false, length = 36)
    private String rentId;

    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "gst_percentae", nullable = false, precision = 10, scale = 2)
    private BigDecimal gstPercentage;

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

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
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
    
}
