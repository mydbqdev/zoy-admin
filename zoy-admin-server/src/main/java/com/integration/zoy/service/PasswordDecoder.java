package com.integration.zoy.service;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordDecoder {
	@Value("${descrypt.secret}")
	private String decryptSecret;

	public  String decryptedText(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String secret=decryptSecret;

		byte[] cipherData = Base64.getDecoder().decode(cipherText);
		byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

		MessageDigest md5  = MessageDigest.getInstance("MD5");

		final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
		SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
		IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

		byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
		Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] decryptedData = aesCBC.doFinal(encrypted);
		String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

		return decryptedText;
	}

	public byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {
		int digestLength = md.getDigestLength();
		int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
		byte[] generatedData = new byte[requiredLength];
		int generatedLength = 0;

		try {
			md.reset();

			while (generatedLength < keyLength + ivLength) {

				if (generatedLength > 0)
					md.update(generatedData, generatedLength - digestLength, digestLength);
				md.update(password);
				if (salt != null)
					md.update(salt, 0, 8);
				md.digest(generatedData, generatedLength, digestLength);

				for (int i = 1; i < iterations; i++) {
					md.update(generatedData, generatedLength, digestLength);
					md.digest(generatedData, generatedLength, digestLength);
				}
				generatedLength += digestLength;
			}

			byte[][] result = new byte[2][];
			result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
			if (ivLength > 0)
				result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

			return result;

		} catch (DigestException e) {
			throw new RuntimeException(e);

		} finally {
			Arrays.fill(generatedData, (byte)0);
		}
	}

	public String  genaratePassword(int numberOfChar){
		String CapitalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String SmallChars = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String values = CapitalChars + SmallChars +numbers;
		Random random = new Random();
		StringBuilder password = new StringBuilder();
		for (int i = 0; i <= numberOfChar; i++)
		{
			password.append(values.charAt(random.nextInt(values.length())));
		}
		return password.toString();

	}

	public String encryptedText(String plainText) throws Exception {
		byte[] salt = new byte[16];  
		new java.security.SecureRandom().nextBytes(salt);

		MessageDigest md5 = MessageDigest.getInstance("MD5");
		final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, Arrays.copyOfRange(salt, 8, 16), decryptSecret.getBytes(StandardCharsets.UTF_8), md5); 
		SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
		IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

		Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] encryptedData = aesCBC.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

		byte[] result = new byte[salt.length + encryptedData.length];
		System.arraycopy(salt, 0, result, 0, salt.length); 
		System.arraycopy(encryptedData, 0, result, salt.length, encryptedData.length); 

		return Base64.getEncoder().encodeToString(result);
	}
}
