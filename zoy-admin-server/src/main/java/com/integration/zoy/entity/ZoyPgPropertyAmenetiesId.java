package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgPropertyAmenetiesId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="property_id")
    private String propertyId;
	@Column(name="ameneties_id")
    private String amenetiesId;

    @Override
	public int hashCode() {
		return Objects.hash(amenetiesId, propertyId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgPropertyAmenetiesId other = (ZoyPgPropertyAmenetiesId) obj;
		return Objects.equals(amenetiesId, other.amenetiesId) && Objects.equals(propertyId, other.propertyId);
	}

	public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAmenetiesId() {
        return amenetiesId;
    }

    public void setAmenetiesId(String amenetiesId) {
        this.amenetiesId = amenetiesId;
    }

    
}
