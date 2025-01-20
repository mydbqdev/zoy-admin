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
@Table(name = "zoy_pg_rent_gst", schema = "pgowners")
public class ZoyPgGstCharges {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "rent_id", updatable = false, nullable = false, unique = true, length = 36)
	private String rentId;
	
	@Column(name = "component_name", nullable = false)
	private String componentName;

	@Column(name = "created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name = "gst_percentae", nullable = false, precision = 10, scale = 2)
	private BigDecimal gstPecrentage;
	
	@Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
	private BigDecimal monthlyRent;

	public String getRentId() {
		return rentId;
	}

	public void setRentId(String rentId) {
		this.rentId = rentId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public BigDecimal getGstPecrentage() {
		return gstPecrentage;
	}

	public void setGstPecrentage(BigDecimal gstPecrentage) {
		this.gstPecrentage = gstPecrentage;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(BigDecimal monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
	

}
