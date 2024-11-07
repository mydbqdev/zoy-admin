package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgPropertyDuesId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="property_id")
    private String propertyId;
	@Column(name="due_id")
    private String dueId;
	@Column(name="factor_id")
    private String factorId;
	

    @Override
	public int hashCode() {
		return Objects.hash(dueId, factorId, propertyId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgPropertyDuesId other = (ZoyPgPropertyDuesId) obj;
		return Objects.equals(dueId, other.dueId) && Objects.equals(factorId, other.factorId)
				&& Objects.equals(propertyId, other.propertyId);
	}

	public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getDueId() {
        return dueId;
    }

    public void setDueId(String dueId) {
        this.dueId = dueId;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }


}
