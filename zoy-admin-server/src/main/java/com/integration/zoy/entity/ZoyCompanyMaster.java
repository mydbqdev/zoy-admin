package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
@Entity
@Table(name = "zoy_company_master", schema = "pgadmin")
public class ZoyCompanyMaster {
	@Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "company_master_id",updatable = false, nullable = false, unique = true, length = 36)
	private String companyMasterId;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "gst_number")
	private String gstNumber;
	
	@Column(name = "pan_number")
	private String panNumber;
	
	@Column(name = "url")
	private String url;
	
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Column(name = "company_logo", columnDefinition = "bytea")
	private byte[] companyLogo;

	public String getCompanyMasterId() {
		return companyMasterId;
	}

	public void setCompanyMasterId(String companyMasterId) {
		this.companyMasterId = companyMasterId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(byte[] companyLogo) {
		this.companyLogo = companyLogo;
	}
	
	
	
}
