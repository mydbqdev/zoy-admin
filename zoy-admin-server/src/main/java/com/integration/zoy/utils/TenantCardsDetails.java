package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class TenantCardsDetails {
	@SerializedName("activeTenantsCount")
    private Long activeTenantsCount;

    @SerializedName("upcomingTenantsCount")
    private Long upcomingTenantsCount;

    @SerializedName("inactiveTenantsCount")
    private Long inactiveTenantsCount;

    @SerializedName("suspendedTenantsCount")
    private Long suspendedTenantsCount;

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

    public Long getSuspendedTenantsCount() {
        return suspendedTenantsCount;
    }

    public void setSuspendedTenantsCount(Long suspendedTenantsCount) {
        this.suspendedTenantsCount = suspendedTenantsCount;
    }

    public TenantCardsDetails(Long activeTenantsCount, Long upcomingTenantsCount, Long inactiveTenantsCount, Long suspendedTenantsCount) {
        super();
        this.activeTenantsCount = activeTenantsCount;
        this.upcomingTenantsCount = upcomingTenantsCount;
        this.inactiveTenantsCount = inactiveTenantsCount;
        this.suspendedTenantsCount = suspendedTenantsCount;
    }
}
