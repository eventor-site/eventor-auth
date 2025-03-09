package com.eventorauth.global.config;

import java.util.Collections;
import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;

import com.eventorauth.auth.client.UserClient;
import com.eventorauth.auth.filter.CustomFilter;
import com.eventorauth.auth.filter.LoginFilter;
import com.eventorauth.auth.filter.LogoutFilter;
import com.eventorauth.auth.repository.RefreshTokenRepository;
import com.eventorauth.auth.service.AppCustomUserDetailsService;
import com.eventorauth.auth.utils.JwtUtils;
import com.eventorauth.global.util.CookieUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final CookieUtils cookieUtils;
	private final JwtUtils jwtUtils;
	private final AppCustomUserDetailsService userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserClient userClient;
	// private final CustomOAuth2UserService customOAuth2UserService;
	// private final CustomSuccessHandler customSuccessHandler;

	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests((auth) -> auth
					.requestMatchers("/**", "/js/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
				// .anyRequest().authenticated()
			)

			.addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)

			//
			// .oauth2Login((oauth2) -> oauth2
			//
			// 	.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
			// 		.userService(customOAuth2UserService))
			// 	.successHandler(customSuccessHandler)
			//
			// )

			// .addFilterAfter(new JWTFilter(jwtUtils), OAuth2LoginAuthenticationFilter.class)

			.addFilterAt(
				new LoginFilter(
					authenticationManager(authenticationConfiguration),
					jwtUtils,
					accessTokenExpiresIn,
					refreshTokenExpiresIn,
					refreshTokenRepository,
					userClient
				),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new LogoutFilter(cookieUtils, refreshTokenRepository),
				org.springframework.security.web.authentication.logout.LogoutFilter.class)
			.userDetailsService(userDetailsService)

			.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(List.of("http://localhost:8090", "https://www.eventor.store"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);

				configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
				configuration.setExposedHeaders(Collections.singletonList("Authorization"));

				return configuration;
			}))
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
