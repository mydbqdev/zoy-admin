package com.integration.zoy.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.config.DisabledUserException;
import com.integration.zoy.entity.AdminUserLoginDetails;


@Service
public class AdminUserAuthService implements UserDetailsService {

	@Autowired
	AdminDBImpl adminDBImpl;
	
	@Value("${app.zoy.username}")
	private String username;

	@Value("${app.zoy.password}")
	private String password;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		if (username.equals(email)) {
            return new org.springframework.security.core.userdetails.User(
                username, 
                password,
                getUserAuthority(List.of("ADMIN"))
            ); 
        }
		
		UserDetails userDetail;
		List<String> roleList=new ArrayList<String>();
		AdminUserLoginDetails user = adminDBImpl.findByEmail(email);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + email);
		}else if(!user.getIsActive()) {
			throw new DisabledUserException("User Inactive");
		}else {
			List<String> roles = adminDBImpl.findUserRoles(email);
        	
        	roles.forEach(str ->{
        		roleList.add(str);
        	});
		}
		List<GrantedAuthority> authorities = getUserAuthority(roleList);
		userDetail=buildUserForAuthentication(user, authorities);
		return userDetail;
	}


	private List<GrantedAuthority> getUserAuthority(List<String> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (String role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(AdminUserLoginDetails user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getPassword(), authorities);
    }

}