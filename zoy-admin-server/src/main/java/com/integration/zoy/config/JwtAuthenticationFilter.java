package com.integration.zoy.config;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.integration.zoy.service.AdminUserAuthService;
import com.integration.zoy.service.ZoyAdminService;

import io.jsonwebtoken.ExpiredJwtException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AdminUserAuthService adminAuthService;

	@Autowired
	private ZoyAdminService zoyAdminService; 
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		try {
			if (header == null || !header.startsWith("Bearer ")) {
				throw new JwtTokenMissingException("No JWT token found in the request headers");
			}
			String token = header.replace("Bearer ","");
			if (zoyAdminService.isBlacklisted(token)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired or blacklisted");
				return;
			} else {
				if(jwtUtil.validateToken(token)) {
					String email = jwtUtil.getUserName(token);
					UserDetails userDetails = adminAuthService.loadUserByUsername(email);
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				} else {
					System.out.println("Can't set security context");
				}
			}
		}catch(ExpiredJwtException ex) {
			request.setAttribute("exception",ex);
		}catch(BadCredentialsException ex) {
			request.setAttribute("exception",ex);
		}catch(Exception ex) {
			request.setAttribute("exception",ex);
		}
		filterChain.doFilter(request, response);
	}

}