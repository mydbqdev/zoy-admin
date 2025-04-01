package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class PgOwnerCardDetails {
	
	
	@SerializedName("leadOwnersCount")
    private Long leadOwnersCount;

    @SerializedName("zoyCodeGeneratedOwnersCount")
    private Long zoyCodeGeneratedOwnersCount;

    @SerializedName("ownerAppUsersCount")
    private Long ownerAppUsersCount;

    @SerializedName("ZeroPropertyOwnersCount")
    private Long ZeroPropertyOwnersCount;

	public Long getLeadOwnersCount() {
		return leadOwnersCount;
	}

	public void setLeadOwnersCount(Long leadOwnersCount) {
		this.leadOwnersCount = leadOwnersCount;
	}

	public Long getZoyCodeGeneratedOwnersCount() {
		return zoyCodeGeneratedOwnersCount;
	}

	public void setZoyCodeGeneratedOwnersCount(Long zoyCodeGeneratedOwnersCount) {
		this.zoyCodeGeneratedOwnersCount = zoyCodeGeneratedOwnersCount;
	}

	public Long getOwnerAppUsersCount() {
		return ownerAppUsersCount;
	}

	public void setOwnerAppUsersCount(Long ownerAppUsersCount) {
		this.ownerAppUsersCount = ownerAppUsersCount;
	}

	public Long getZeroPropertyOwnersCount() {
		return ZeroPropertyOwnersCount;
	}

	public void setZeroPropertyOwnersCount(Long zeroPropertyOwnersCount) {
		ZeroPropertyOwnersCount = zeroPropertyOwnersCount;
	}

	public PgOwnerCardDetails(Long leadOwnersCount, Long zoyCodeGeneratedOwnersCount, Long ownerAppUsersCount,
			Long zeroPropertyOwnersCount) {
		super();
		this.leadOwnersCount = leadOwnersCount;
		this.zoyCodeGeneratedOwnersCount = zoyCodeGeneratedOwnersCount;
		this.ownerAppUsersCount = ownerAppUsersCount;
		ZeroPropertyOwnersCount = zeroPropertyOwnersCount;
	}
    
    

}
