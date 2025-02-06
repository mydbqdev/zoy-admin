package com.integration.zoy.model;

public class PgOwnerProfile {
	private String ownerName;
	private String ownerID;
	private String status;
	private String profilePhoto;
	private String zoyShare;
	

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getZoyShare() {
		return zoyShare;
	}

	public void setZoyShare(String zoyShare) {
		this.zoyShare = zoyShare;
	}

}
