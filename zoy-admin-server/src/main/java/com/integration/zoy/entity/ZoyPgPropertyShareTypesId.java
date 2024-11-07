package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ZoyPgPropertyShareTypesId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="share_id")
    private String shareId;
	@Column(name="property_id")
    private String propertyId;

    @Override
	public int hashCode() {
		return Objects.hash(propertyId, shareId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoyPgPropertyShareTypesId other = (ZoyPgPropertyShareTypesId) obj;
		return Objects.equals(propertyId, other.propertyId) && Objects.equals(shareId, other.shareId);
	}

	public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

   
}
