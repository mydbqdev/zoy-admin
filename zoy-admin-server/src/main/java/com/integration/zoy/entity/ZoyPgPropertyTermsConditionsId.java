package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgPropertyTermsConditionsId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="property_id")
	private String propertyId;
	@Column(name="term_id")
	private String termId;

	@Override
	public int hashCode() {
		return Objects.hash(propertyId, termId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgPropertyTermsConditionsId other = (ZoyPgPropertyTermsConditionsId) obj;
		return Objects.equals(propertyId, other.propertyId) && Objects.equals(termId, other.termId);
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	@Override
	public String toString() {
		return "ZoyPgPropertyTermsConditionsId [propertyId=" + propertyId + ", termId=" + termId + "]";
	}
	
	

}