package com.integration.zoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.PgAreaCode;
import com.integration.zoy.entity.PgLocationCode;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.repository.PgAreaCodeRepository;
import com.integration.zoy.repository.PgLocationCodeRepository;
import com.integration.zoy.repository.UserProfileRepository;

@Service
public class CommonDBService implements CommonDBImpl {

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private PgLocationCodeRepository pgLocationCodeRepository;
	
	@Autowired
	private PgAreaCodeRepository pgAreaCodeRepository;

	@Override
	public UserProfile registerNewUserAccount(UserProfile user) {
		return userProfileRepository.save(user);
	}

	@Override
	public UserProfile findByVerifyToken(String token) {
		return userProfileRepository.findByVerifyToken(token).orElse(null);
	}

	@Override
	public boolean validateRegisterEmail(String email) {
		UserProfile user= userProfileRepository.findRegisterEmail(email).orElse(null);
		if(user!=null)
			return true;
		else 
			return false;
	}

	@Override
	public UserProfile findRegisterEmail(String email) {
		return userProfileRepository.findRegisterEmail(email).orElse(null);
	}

	@Override
	public List<PgLocationCode> saveLocationCode(List<PgLocationCode> codes) {
		return pgLocationCodeRepository.saveAll(codes);
	}

	@Override
	public List<PgAreaCode> saveAreaCode(List<PgAreaCode> areas) {
		return pgAreaCodeRepository.saveAll(areas);
	}

	@Override
	public PgLocationCode saveLocationCode(PgLocationCode code) {
		return pgLocationCodeRepository.save(code);
	}

	@Override
	public PgAreaCode saveAreaCode(PgAreaCode code) {
		return pgAreaCodeRepository.save(code);
	}


}
