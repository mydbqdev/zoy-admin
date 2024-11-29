package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class SuperAdminCardsDetails {

	@SerializedName("usersWithNonNullPin")
	private Long usersWithNonNullPin;

	@SerializedName("activeOwnersCount")
	private Long activeOwnersCount;

	@SerializedName("activePropertiesCount")
	private Long activePropertiesCount;

	@SerializedName("zoyShare")
	private Long zoyShare;

	public Long getUsersWithNonNullPin() {
		return usersWithNonNullPin;
	}
	public void setUsersWithNonNullPin(Long usersWithNonNullPin) {
		this.usersWithNonNullPin = usersWithNonNullPin;
	}
	public Long getActiveOwnersCount() {
		return activeOwnersCount;
	}
	public void setActiveOwnersCount(Long activeOwnersCount) {
		this.activeOwnersCount = activeOwnersCount;
	}
	public Long getActivePropertiesCount() {
		return activePropertiesCount;
	}
	public void setActivePropertiesCount(Long activePropertiesCount) {
		this.activePropertiesCount = activePropertiesCount;
	}

	public Long getZoyShare() {
		return zoyShare;
	}
	public void setZoyShare(Long zoyShare) {
		this.zoyShare = zoyShare;
	}
	public SuperAdminCardsDetails(Long usersWithNonNullPin, Long activeOwnersCount, Long activePropertiesCount,
			Long zoyShare) {
		super();
		this.usersWithNonNullPin = usersWithNonNullPin;
		this.activeOwnersCount = activeOwnersCount;
		this.activePropertiesCount = activePropertiesCount;
		this.zoyShare = zoyShare;
	}
	

}
