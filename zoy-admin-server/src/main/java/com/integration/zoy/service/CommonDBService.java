package com.integration.zoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.repository.UserProfileRepository;

@Service
public class CommonDBService implements CommonDBImpl {

	@Autowired
	private UserProfileRepository userProfileRepository;


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


}
