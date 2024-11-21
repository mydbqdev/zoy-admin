package com.integration.zoy.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

@Service
public class ZoyCodeGenerationService {
	
	public String generateZoyCode(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(email.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext; 
            }
            return "ZOY" +( hashtext.substring(0, 9).toUpperCase());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
