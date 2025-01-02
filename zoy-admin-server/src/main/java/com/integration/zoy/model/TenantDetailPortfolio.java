package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

public class TenantDetailPortfolio {
	@SerializedName("profile")
	private TenantProfile profile;
	@SerializedName("activeBookings")
	private ActiveBookings activeBookings;	
//	private List<ClosedBookings> closedBookings;
//	private List<UpComingBookings> upComingBookings;
	public TenantProfile getProfile() {
		return profile;
	}
	public void setProfile(TenantProfile profile) {
		this.profile = profile;
	}
	public ActiveBookings getActiveBookings() {
		return activeBookings;
	}
	public void setActiveBookings(ActiveBookings activeBookings) {
		this.activeBookings = activeBookings;
	}

}
