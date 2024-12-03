package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "zoy_share_master", schema = "pgcommon")
public class ZoyShareMaster {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "zoy_share_id", updatable = false, nullable = false, unique = true, length = 36)
	private String zoyShareId;

	@Column(name = "property_share", nullable = false, precision = 10, scale = 2)
	private BigDecimal propertyShare;

	@Column(name = "zoy_share", nullable = false, precision = 10, scale = 2)
	private BigDecimal zoyShare;

	// Getters and Setters

	public String getZoyShareId() {
		return zoyShareId;
	}

	public void setZoyShareId(String zoyShareId) {
		this.zoyShareId = zoyShareId;
	}

	public BigDecimal getPropertyShare() {
		return propertyShare;
	}

	public void setPropertyShare(BigDecimal propertyShare) {
		this.propertyShare = propertyShare;
	}

	public BigDecimal getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(BigDecimal zoyShare) {
		this.zoyShare = zoyShare;
	}
}