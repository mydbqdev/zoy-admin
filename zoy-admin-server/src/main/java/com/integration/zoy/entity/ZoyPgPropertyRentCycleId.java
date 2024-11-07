package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgPropertyRentCycleId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="property_id")
    private String propertyId;
	@Column(name="cycle_id")
    private String cycleId;
	
	
    @Override
	public int hashCode() {
		return Objects.hash(cycleId, propertyId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgPropertyRentCycleId other = (ZoyPgPropertyRentCycleId) obj;
		return Objects.equals(cycleId, other.cycleId) && Objects.equals(propertyId, other.propertyId);
	}

	public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

}
