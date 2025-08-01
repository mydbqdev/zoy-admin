package com.integration.zoy.service;

import java.util.List;

import com.integration.zoy.entity.PgAreaCode;
import com.integration.zoy.entity.PgLocationCode;
import com.integration.zoy.entity.UserProfile;

public interface CommonDBImpl {

	
	UserProfile registerNewUserAccount(UserProfile user);
	UserProfile findByVerifyToken(String token);
	boolean validateRegisterEmail(String email);
	UserProfile findRegisterEmail(String email);
	List<PgLocationCode> saveLocationCode(List<PgLocationCode> codes);
	List<PgAreaCode> saveAreaCode(List<PgAreaCode> areas);
	PgLocationCode saveLocationCode(PgLocationCode code);
	PgAreaCode saveAreaCode(PgAreaCode code);

	
	
}
