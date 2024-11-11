package com.eventorauth.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventorauth.auth.dto.entity.RefreshToken;
import com.eventorauth.auth.dto.request.ReissueTokenRequest;
import com.eventorauth.auth.dto.response.ReissueTokensResponse;
import com.eventorauth.auth.repository.RefreshTokenRepository;
import com.eventorauth.auth.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 및 토큰 관리를 담당하는 서비스 클래스입니다.
 * JWT 를 사용하여 액세스 토큰과 리프레시 토큰을 생성, 갱신하고, Redis 를 통해 리프레시 토큰을 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtils jwtUtils;

	@Setter
	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;

	@Setter
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	/**
	 * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
	 * 리프레시 토큰이 유효하지 않거나 Redis 에서 해당 토큰이 존재하지 않으면 null 을 반환합니다.
	 */
	public ReissueTokensResponse reissueTokens(ReissueTokenRequest request) {
		String refreshToken = request.refreshToken();

		if (refreshToken == null || jwtUtils.validateToken(refreshToken) != null) {
			return null;
		}

		String tokenType = jwtUtils.getTokenTypeFromToken(refreshToken);

		// refresh 토큰이 아님 or redis 에 refresh 토큰이 존재하지 않음
		if (!"refresh".equals(tokenType) || !refreshTokenRepository.existsById(refreshToken)) {
			return null;
		}

		Long userId = refreshTokenRepository.findById(refreshToken).get().getUserId();
		List<String> roles = refreshTokenRepository.findById(refreshToken)
			.map(token -> token.getRoles() != null ? token.getRoles() : List.<String>of())
			.orElse(List.of());

		String newAccessToken = jwtUtils.generateAccessToken(userId, roles, accessTokenExpiresIn);
		String newRefreshToken = jwtUtils.generateRefreshToken(refreshTokenExpiresIn);

		refreshTokenRepository.deleteById(refreshToken);
		refreshTokenRepository.save(
			new RefreshToken(newRefreshToken.replace("Bearer ", ""), userId, roles, refreshTokenExpiresIn));

		return ReissueTokensResponse.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

}