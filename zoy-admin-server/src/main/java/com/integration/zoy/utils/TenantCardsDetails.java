package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class TenantCardsDetails {
	@SerializedName("activeTenantsCount")
    private Long activeTenantsCount;

    @SerializedName("upcomingTenantsCount")
    private Long upcomingTenantsCount;

    @SerializedName("inactiveTenantsCount")
    private Long inactiveTenantsCount;

    @SerializedName("registerTenantsCount")
    private Long registerTenantsCount;

    public Long getActiveTenantsCount() {
		return activeTenantsCount;
	}

	public void setActiveTenantsCount(Long activeTenantsCount) {
		this.activeTenantsCount = activeTenantsCount;
	}

	public Long getUpcomingTenantsCount() {
        return upcomingTenantsCount;
    }

    public void setUpcomingTenantsCount(Long upcomingTenantsCount) {
        this.upcomingTenantsCount = upcomingTenantsCount;
    }

    public Long getInactiveTenantsCount() {
        return inactiveTenantsCount;
    }

    public void setInactiveTenantsCount(Long inactiveTenantsCount) {
        this.inactiveTenantsCount = inactiveTenantsCount;
    }

    public Long getRegisterTenantsCount() {
		return registerTenantsCount;
	}

	public void setRegisterTenantsCount(Long registerTenantsCount) {
		this.registerTenantsCount = registerTenantsCount;
	}

	public TenantCardsDetails(Long activeTenantsCount, Long upcomingTenantsCount, Long inactiveTenantsCount, Long registerTenantsCount) {
        super();
        this.activeTenantsCount = activeTenantsCount;
        this.upcomingTenantsCount = upcomingTenantsCount;
        this.inactiveTenantsCount = inactiveTenantsCount;
        this.registerTenantsCount = registerTenantsCount;
    }
}
