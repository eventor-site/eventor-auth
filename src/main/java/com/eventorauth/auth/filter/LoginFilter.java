package com.eventorauth.auth.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eventorauth.auth.client.UserClient;
import com.eventorauth.auth.dto.entity.RefreshToken;
import com.eventorauth.auth.dto.request.LoginRequest;
import com.eventorauth.auth.dto.request.UpdateLastLoginTimeRequest;
import com.eventorauth.auth.dto.response.LoginResponse;
import com.eventorauth.auth.repository.RefreshTokenRepository;
import com.eventorauth.auth.utils.JwtUtils;
import com.eventorauth.global.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 요청을 처리하는 필터입니다. 이 필터는 사용자의 로그인 요청을 인증하고, 성공적으로 인증된 경우 JWT 를 생성하여 클라이언트에게 반환하며, 리프레시 토큰을 Redis 에 저장합니다.
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final Long accessTokenExpiresIn;
	private final Long refreshTokenExpiresIn;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserClient userClient;

	/**
	 * 생성자입니다. 로그인 필터를 설정합니다.
	 */
	public LoginFilter(
		AuthenticationManager authenticationManager, JwtUtils jwtUtils, Long accessTokenExpiresIn,
		Long refreshTokenExpiresIn, RefreshTokenRepository refreshTokenRepository, UserClient userClient) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.accessTokenExpiresIn = accessTokenExpiresIn;
		this.refreshTokenExpiresIn = refreshTokenExpiresIn;
		this.refreshTokenRepository = refreshTokenRepository;
		this.userClient = userClient;
		setFilterProcessesUrl("/auth/login");
	}

	/**
	 * 로그인 요청을 인증합니다.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		LoginRequest loginRequest;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			String identifier = loginRequest.identifier();
			String password = loginRequest.password();

			UsernamePasswordAuthenticationToken authToken
				= new UsernamePasswordAuthenticationToken(identifier, password, null);

			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 인증에 성공한 후 호출됩니다. JWT 를 생성하고 응답에 작성합니다.
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {
		Long userId = Long.parseLong(authentication.getName());
		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		String accessToken = jwtUtils.generateAccessToken(userId, roles, accessTokenExpiresIn);
		String refreshToken = jwtUtils.generateRefreshToken(refreshTokenExpiresIn);

		refreshTokenRepository.save(
			new RefreshToken(refreshToken.replace("Bearer ", ""), userId, roles, refreshTokenExpiresIn));

		userClient.updateLastLoginTime(new UpdateLastLoginTimeRequest(userId, LocalDateTime.now()));

		LoginResponse loginResponse = LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // Registering the JavaTimeModule
		String loginResponseJson = objectMapper.writeValueAsString(ApiResponse.createSuccess(loginResponse));

		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(loginResponseJson);
	}

	/**
	 * 인증에 실패한 후 호출됩니다. 오류 메시지를 응답에 작성합니다.
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String errorMessage = "{\"message\": \"인증 실패\"}";
		response.getWriter().write(errorMessage);
	}
}
