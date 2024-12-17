package com.integration.zoy.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadProfile {

	private MultipartFile profilePicture;

	private byte[] userProfilePicture;
	
	public byte[] getUserProfilePicture() {
		return userProfilePicture;
	}

	public void setUserProfilePicture(byte[] userProfilePicture) {
		this.userProfilePicture = userProfilePicture;
	}

	public MultipartFile getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(MultipartFile profilePicture) {
		this.profilePicture = profilePicture;
	}

}
