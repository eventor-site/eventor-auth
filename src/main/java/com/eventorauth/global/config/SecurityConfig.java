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

import com.sikyeojoauth.auth.filter.LoginFilter;
import com.sikyeojoauth.auth.filter.LogoutFilter;
import com.sikyeojoauth.auth.repository.RefreshTokenRepository;
import com.sikyeojoauth.auth.service.AppCustomUserDetailsService;
import com.sikyeojoauth.auth.utils.JwtUtils;
import com.sikyeojoauth.global.util.CookieUtils;

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
					refreshTokenRepository
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
