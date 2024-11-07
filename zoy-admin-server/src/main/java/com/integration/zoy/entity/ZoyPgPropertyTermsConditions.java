package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zoy_pg_property_terms_conditions", schema = "pgowners")
public class ZoyPgPropertyTermsConditions {

    @EmbeddedId
    private ZoyPgPropertyTermsConditionsId id;

    public ZoyPgPropertyTermsConditionsId getId() {
        return id;
    }

    public void setId(ZoyPgPropertyTermsConditionsId id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "ZoyPgPropertyTermsConditions [id=" + id + "]";
	}

}