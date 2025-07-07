package com.integration.zoy.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.common.collect.ImmutableList;
import com.integration.zoy.service.AdminUserAuthService;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Autowired
	AdminUserAuthService adminUserAuthService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Value("${web.origin.link}")
	private String origin;

	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(adminUserAuthService).passwordEncoder(passwordEncoder());
		return authenticationManagerBuilder.build();
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
	  //  http.cors(cors -> cors.configurationSource(request -> buildConfig()));
	    http.cors(cors -> cors.configurationSource(corsConfigurationSource())); 
	    http.csrf(csrf -> csrf.disable()).sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    .authorizeHttpRequests(requests -> {
	        try {
	            requests.antMatchers("/public").permitAll()
			    .antMatchers("/actuator/**").permitAll()
	            .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/api-docs/**").permitAll()
	            .antMatchers("/verify-email").permitAll()
	            .antMatchers("/forgotPassword").permitAll()
	            .antMatchers("/forgot-password").permitAll()
	            .antMatchers("/zoy_admin/login").permitAll()
	            .antMatchers("/zoy_admin/userSoftlogout").permitAll()
	            .antMatchers("/notificationPageHandler").permitAll()
	            .antMatchers("/admin_reset_password").permitAll()
	            .antMatchers("/zoy_admin/savePgOwnerData").permitAll()
	            .antMatchers("/zoy_admin/location_code").permitAll()
	            .antMatchers("/zoy_admin/saveExistingPgOwnerData").permitAll()
	            .antMatchers("/zoy_admin/resendPgOwnerData").permitAll()
	            .antMatchers("/zoy_admin/resendExistingPgOwnerData").permitAll()
	            .anyRequest().authenticated()
	            .and()
	            .exceptionHandling(handling -> handling
	                    .authenticationEntryPoint(customAuthenticationEntryPoint)
	                    .accessDeniedHandler(customAccessDeniedHandler));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    })
	    .httpBasic(withDefaults());
	    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}



	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		//corsConfiguration.addAllowedOrigin("*"); 
		corsConfiguration.addAllowedOrigin(origin);
		corsConfiguration.addAllowedHeader("*"); 
		corsConfiguration.addAllowedMethod("*"); 
		return corsConfiguration;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
	//	configuration.setAllowedOrigins(ImmutableList.of("*"));
		configuration.setAllowedOrigins(ImmutableList.of(origin));  
		configuration.setAllowedMethods(ImmutableList.of("HEAD","GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
