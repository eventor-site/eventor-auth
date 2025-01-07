package com.eventorauth.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eventorauth.auth.client.UserInfoClient;
import com.eventorauth.auth.filter.LoginFilter;
import com.eventorauth.auth.filter.LogoutFilter;
import com.eventorauth.auth.repository.RefreshTokenRepository;
import com.eventorauth.auth.service.AppCustomUserDetailsService;
import com.eventorauth.auth.utils.JwtUtils;
import com.eventorauth.global.util.CookieUtils;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final CookieUtils cookieUtils;
	private final JwtUtils jwtUtils;
	private final AppCustomUserDetailsService userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserInfoClient userInfoClient;

	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/auth/login", "/auth/reissue", "/auth/logout").permitAll()
				.anyRequest().permitAll()
			)
			// .addFilterBefore(new JwtFilter(jwtUtils), LoginFilter.class)
			.addFilterAt(
				new LoginFilter(
					authenticationManager(authenticationConfiguration),
					jwtUtils,
					accessTokenExpiresIn,
					refreshTokenExpiresIn,
					refreshTokenRepository,
					userInfoClient
				),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new LogoutFilter(cookieUtils, refreshTokenRepository),
				org.springframework.security.web.authentication.logout.LogoutFilter.class)
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.userDetailsService(userDetailsService)
			.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
