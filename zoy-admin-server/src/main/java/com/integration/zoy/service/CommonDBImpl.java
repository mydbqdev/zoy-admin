package com.integration.zoy.service;

import com.integration.zoy.entity.UserProfile;

public interface CommonDBImpl {

	
	UserProfile registerNewUserAccount(UserProfile user);
	UserProfile findByVerifyToken(String token);
	boolean validateRegisterEmail(String email);
	UserProfile findRegisterEmail(String email);

	
	
}
