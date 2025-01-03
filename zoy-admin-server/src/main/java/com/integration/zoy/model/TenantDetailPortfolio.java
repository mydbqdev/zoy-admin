package com.integration.zoy.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TenantDetailPortfolio {
	@SerializedName("profile")
	private TenantProfile profile;
	@SerializedName("activeBookings")
	private ActiveBookings activeBookings;
	@SerializedName("closedBookings")
    private List<CancellationDetails> closedBookings;
    @SerializedName("upcomingBookings")
    private List<UpcomingBookingDetails> upcomingBookings;
    
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
	public List<CancellationDetails> getClosedBookings() {
		return closedBookings;
	}
	public void setClosedBookings(List<CancellationDetails> closedBookings) {
		this.closedBookings = closedBookings;
	}
	public List<UpcomingBookingDetails> getUpcomingBookings() {
		return upcomingBookings;
	}
	public void setUpcomingBookings(List<UpcomingBookingDetails> upcomingBookings) {
		this.upcomingBookings = upcomingBookings;
	}

}
