package com.integration.zoy.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.PgOwnerMaster;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.PgOwnerMasterModel;

@Service
public class ZoyCodeGenerationService {
	
	@Autowired
	OwnerDBImpl ownerDBImpl;

	public String generateZoyCode(PgOwnerMasterModel model) {
		try {
			StringBuffer buffer=new StringBuffer();
			buffer.append(getShortCode(model.getPropertyCity()));
			buffer.append(getShortCode(model.getPropertyLocality()));
			Integer count=ownerDBImpl.findZoyCodeCounter(buffer.toString());
			buffer.append(count+1);
			return buffer.toString(); 
		} catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}
	
//	public String generateZoyCode(PgOwnerMasterModel model) {
//		try {
//			String input = model.getEmailId() + System.currentTimeMillis();
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			byte[] messageDigest = md.digest(input.getBytes());
//			BigInteger no = new BigInteger(1, messageDigest);
//			String hashtext = no.toString(16); 
//			while (hashtext.length() < 32) {
//				hashtext = "0" + hashtext; 
//			}
//			StringBuffer buffer=new StringBuffer();
//			buffer.append("ZOY");
//			buffer.append(model.getPropertyStateShortName().toUpperCase());
//			if(ZoyConstant.CITIES.get(model.getPropertyCity())!=null) {
//				buffer.append(ZoyConstant.CITIES.get(model.getPropertyCity()));
//			} else {
//				buffer.append(getShortCode(model.getPropertyCity()));
//			}
//			buffer.append(getShortCode(model.getPropertyLocality()));
//			return buffer.toString(); 
//
//		} catch (Exception e) {
//			new ZoyAdminApplicationException(e, "");
//		}
//		return null;
//	}



	//	public static String getShortCode(String location) {
	//		String cleaned = location.replaceAll("[^a-zA-Z ]", "").trim();
	//		String[] words = cleaned.split("\\s+");
	//		StringBuilder sb = new StringBuilder();
	//		if (words.length == 1) {
	//			String word = words[0].toUpperCase();
	//			if (word.length() >= 3) 
	//				return word.substring(0, 3).toUpperCase();
	//			sb.append(word);
	//		} else {
	//			for (String word : words) {
	//				if (!word.isEmpty()) {
	//					sb.append(Character.toUpperCase(word.charAt(0)));
	//				}
	//			}
	//		}
	//		while (sb.length() < 3) {
	//			sb.append('X'); 
	//		}
	//		if (sb.length() > 3) {
	//			return sb.substring(0, 3).toUpperCase();
	//		}
	//		return sb.toString().toUpperCase();
	//	}
	public static String getShortCode(String location) {
		return location.substring(0, 3).toUpperCase();
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
