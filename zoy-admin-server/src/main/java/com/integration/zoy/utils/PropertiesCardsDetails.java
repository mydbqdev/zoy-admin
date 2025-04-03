package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class PropertiesCardsDetails {
	
	@SerializedName("potentialProperties")
    private Long potentialProperties;
	
	@SerializedName("nonPotentialProperties")
    private Long nonPotentialProperties;
	
	@SerializedName("upComingpotentialProperties")
    private Long upComingpotentialProperties;

	public Long getPotentialProperties() {
		return potentialProperties;
	}

	public void setPotentialProperties(Long potentialProperties) {
		this.potentialProperties = potentialProperties;
	}

	public Long getNonPotentialProperties() {
		return nonPotentialProperties;
	}

	public void setNonPotentialProperties(Long nonPotentialProperties) {
		this.nonPotentialProperties = nonPotentialProperties;
	}

	public Long getUpComingpotentialProperties() {
		return upComingpotentialProperties;
	}

	public void setUpComingpotentialProperties(Long upComingpotentialProperties) {
		this.upComingpotentialProperties = upComingpotentialProperties;
	}

	public PropertiesCardsDetails(Long potentialProperties, Long nonPotentialProperties,
			Long upComingpotentialProperties) {
		super();
		this.potentialProperties = potentialProperties;
		this.nonPotentialProperties = nonPotentialProperties;
		this.upComingpotentialProperties = upComingpotentialProperties;
	}
	
	
	
}
