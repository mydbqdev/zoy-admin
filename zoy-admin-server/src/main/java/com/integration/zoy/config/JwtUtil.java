package com.integration.zoy.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.token.validity}")
	private long tokenValidity;

	@Value("${jwt.token.refreshExpireDateInMs}")
	private long refreshExpireDateInMs;

	private Key getSigningKey() {
		if (jwtSecret.length() < 64) {
			throw new IllegalArgumentException("JWT secret must be at least 64 bytes (512 bits) for HS512");
		}
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String getUserName(final String token) {
		try {
			Claims body = Jwts.parser()
					.setSigningKey(getSigningKey())  
					.build()
					.parseClaimsJws(token)
					.getBody();

			return body.getSubject();  
		} catch (JwtException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String generateToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		long nowMillis = System.currentTimeMillis();
		long expMillis = nowMillis + tokenValidity;

		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(nowMillis))
				.setExpiration(new Date(expMillis))
				.signWith(getSigningKey())  
				.compact();
	}

	public String doGenerateRefreshToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + refreshExpireDateInMs))
				.signWith(getSigningKey())  
				.compact();
	}

	public boolean validateToken(final String token) {
		try {
			Jwts.parser()
			.setSigningKey(getSigningKey())  
			.build()
			.parseClaimsJws(token); 
			return true;  
		} catch (JwtException ex) {
			ex.printStackTrace(); 
			return false;
		}
	}
}
