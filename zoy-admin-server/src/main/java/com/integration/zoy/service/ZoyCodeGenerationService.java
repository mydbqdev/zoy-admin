package com.integration.zoy.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.PgOwnerMasterModel;

@Service
public class ZoyCodeGenerationService {
	
	public String generateZoyCode(PgOwnerMasterModel model) {
        try {
        	 String input = model.getEmailId() + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext; 
            }
            return "ZOY" 
            + model.getPropertyStateShortName()
            + generateShortNameFromConsonants(model.getPropertyCity())
            + generateShortNameFromConsonants(model.getPropertyLocality())
            + (hashtext.substring(0, 4).toUpperCase());
        } catch (NoSuchAlgorithmException e) {
            new ZoyAdminApplicationException(e, "");
        }
		return null;
    }
	
	public static String generateShortNameFromConsonants(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return "";
        }
        String upperName = cityName.toUpperCase();
        String vowels = "AEIOU";
        StringBuilder consonants = new StringBuilder();
        for (int i = 0; i < upperName.length(); i++) {
            char c = upperName.charAt(i);
            if (Character.isLetter(c) && vowels.indexOf(c) == -1) {
                consonants.append(c);
            }
        }
        return consonants.substring(0,3).toString();
    }
	
	public String autoGeneratePassword(String firstName) {
		 try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] messageDigest = md.digest(firstName.getBytes());
	            BigInteger no = new BigInteger(1, messageDigest);
	            String hashtext = no.toString(16); 
	            while (hashtext.length() < 32) {
	                hashtext = "0" + hashtext; 
	            }
	            return "ZOY@" +firstName+( hashtext.substring(0, 8).toUpperCase());
	        } catch (NoSuchAlgorithmException e) {
	            new ZoyAdminApplicationException(e, "");
	        }
			return null;
	}
}
