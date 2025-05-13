package com.integration.zoy.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.ImmutableList;
import com.integration.zoy.service.AdminUserAuthService;

@Configuration
@EnableWebSecurity
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
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Apply CORS config
            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Use stateless session management
            .authorizeRequests(authz -> authz
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow OPTIONS (preflight) requests
                .antMatchers("/public").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                .antMatchers("/verify-email").permitAll()
                .antMatchers("/forgotPassword", "/forgot-password").permitAll()
                .antMatchers("/zoy_admin/login").permitAll()
                .antMatchers("/zoy_admin/userSoftlogout").permitAll()
                .antMatchers("/notificationPageHandler").permitAll()
                .antMatchers("/admin_reset_password").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
            .httpBasic(withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use wildcard or specify exact allowed origin(s)
        configuration.setAllowedOrigins(ImmutableList.of("*"));  
        configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);  // Allow credentials (cookies, authorization headers)
        configuration.addAllowedHeader("*");  // Allow all headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> loggingFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                System.out.println("CORS Origin: " + request.getHeader("Origin"));
                System.out.println("CORS Method: " + request.getMethod());
                System.out.println("CORS URL: " + request.getRequestURL());
                        System.out.println(request.getContentType() +" "+ request.getAuthType());
                filterChain.doFilter(request, response);
            }
        });
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
