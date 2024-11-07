package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "browser_locality_sub_level", schema = "pgcommon")
public class BrowserLocalitySubLevel {

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	@Column(name = "browser_id")
    private String browserId;

	@Column(name = "sub_level")
    private String subLevel;
	
	@Column(name = "browser_image_url")
    private String browserImageUrl;
	
	@Column(name = "latitude" , precision = 10, scale = 7)
    private BigDecimal latitude;
	
	@Column(name = "longitude" , precision = 10, scale = 7)
    private BigDecimal longitude;

	public String getBrowserId() {
		return browserId;
	}

	public void setBrowserId(String browserId) {
		this.browserId = browserId;
	}

	public String getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
	}

	public String getBrowserImageUrl() {
		return browserImageUrl;
	}

	public void setBrowserImageUrl(String browserImageUrl) {
		this.browserImageUrl = browserImageUrl;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	
	
	
}
