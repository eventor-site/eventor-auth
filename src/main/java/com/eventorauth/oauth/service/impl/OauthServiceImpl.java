package com.eventorauth.oauth.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eventorauth.auth.client.UserClient;
import com.eventorauth.auth.dto.entity.RefreshToken;
import com.eventorauth.auth.dto.request.SignUpRequest;
import com.eventorauth.auth.dto.request.UpdateLastLoginTimeRequest;
import com.eventorauth.auth.dto.response.GetUserTokenInfoResponse;
import com.eventorauth.auth.repository.RefreshTokenRepository;
import com.eventorauth.auth.utils.JwtUtils;
import com.eventorauth.global.dto.ApiResponse;
import com.eventorauth.global.exception.ServerException;
import com.eventorauth.global.exception.UnauthorizedException;
import com.eventorauth.oauth.client.GoogleProfileClient;
import com.eventorauth.oauth.client.GoogleTokenClient;
import com.eventorauth.oauth.client.KakaoProfileClient;
import com.eventorauth.oauth.client.KakaoTokenClient;
import com.eventorauth.oauth.client.NaverProfileClient;
import com.eventorauth.oauth.client.NaverTokenClient;
import com.eventorauth.oauth.config.OauthAttributes;
import com.eventorauth.oauth.config.OauthProvider;
import com.eventorauth.oauth.dto.OauthDto;
import com.eventorauth.oauth.dto.OauthTokenResponse;
import com.eventorauth.oauth.dto.UserProfile;
import com.eventorauth.oauth.repository.InMemoryOauthRepository;
import com.eventorauth.oauth.service.OauthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {
	private final JwtUtils jwtUtils;
	private final RefreshTokenRepository refreshTokenRepository;
	private final InMemoryOauthRepository inMemoryProviderRepository;
	private final UserClient userClient;

	private final NaverTokenClient naverTokenClient;
	private final NaverProfileClient naverProfileClient;

	private final KakaoTokenClient kakaoTokenClient;
	private final KakaoProfileClient kakaoProfileClient;

	private final GoogleTokenClient googleTokenClient;
	private final GoogleProfileClient googleProfileClient;

	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	@Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
	private String adminKey;

	public String authentication(String registrationId) {
		OauthProvider oauthClient = inMemoryProviderRepository.findByProviderName(registrationId);

		// Redirect URL 생성
		String url = buildRedirectUrl(registrationId, oauthClient);
		return String.format("redirect:%s", url);
	}

	private String buildRedirectUrl(String registrationId, OauthProvider oauthClient) {
		if (registrationId.equals("google")) {
			return String.format(
				"%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
				oauthClient.getAuthorizationUri(),
				oauthClient.getClientId(),
				oauthClient.getRedirectUri(),
				URLEncoder.encode(oauthClient.getScope(), StandardCharsets.UTF_8),
				UUID.randomUUID());
		} else {
			return String.format("%s?response_type=code&client_id=%s&state=%s&redirect_uri=%s",
				oauthClient.getAuthorizationUri(),
				oauthClient.getClientId(),
				UUID.randomUUID(),
				oauthClient.getRedirectUri());
		}
	}

	public SignUpRequest getToken(String registrationId, String code) {

		// 프론트에서 넘어온 provider 이름을 통해 InMemoryProviderRepository 에서 OauthProvider 가져오기
		OauthProvider provider = inMemoryProviderRepository.findByProviderName(registrationId);

		// access token 가져오기
		if (registrationId.equals("naver")) {
			OauthTokenResponse tokenResponse = naverTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getClientSecret(), code, UUID.randomUUID().toString());

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));

			return SignUpRequest.fromUserProfile(userProfile);
		} else if (registrationId.equals("kakao")) {
			OauthTokenResponse tokenResponse = kakaoTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getRedirectUri(), code);

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));
			return SignUpRequest.fromUserProfile(userProfile);
		} else {
			OauthTokenResponse tokenResponse = googleTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getClientSecret(), code, provider.getRedirectUri());

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));
			return SignUpRequest.fromUserProfile(userProfile);
		}

	}

	public Map<String, Object> getUserAttributes(String registrationId, OauthTokenResponse tokenResponse) {
		// "Bearer {accessToken}" 형식으로 Authorization 헤더 설정
		String authorizationHeader = "Bearer " + tokenResponse.getAccessToken();

		// ProfileClient 를 사용하여 사용자 프로필 정보 가져오기
		if (registrationId.equals("naver")) {
			return naverProfileClient.getUserProfile(authorizationHeader);
		} else if (registrationId.equals("kakao")) {
			authorizationHeader = "KakaoAK " + adminKey;

			return kakaoProfileClient.getUserProfile(authorizationHeader, "user_id",
				getSubFromIdToken(tokenResponse.getIdToken()));

		} else if (registrationId.equals("google")) {
			return googleProfileClient.getUserProfile(authorizationHeader);
		}
		return null;
	}

	public static Long getSubFromIdToken(String idToken) {
		try {
			// ID 토큰은 3개의 부분으로 나뉘어져 있음: 헤더.페이로드.서명
			String[] tokenParts = idToken.split("\\.");
			if (tokenParts.length < 2) {
				throw new IllegalArgumentException("Invalid ID token format");
			}

			// Base64 디코딩 후 JSON 형식의 페이로드 추출
			String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));

			// JSON 파싱 후 sub 값 추출
			JSONObject payloadJson = new JSONObject(payload);
			return Long.parseLong(payloadJson.getString("sub"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse sub from ID token", e);
		}
	}

	public ApiResponse<Boolean> existsByOauth(OauthDto request) {
		return userClient.existsByOauth(request).getBody();
	}

	public void oauthSignup(SignUpRequest request) {
		userClient.oauthSignup(request);
	}

	public void oauthLogin(OauthDto request, HttpServletResponse response) {
		GetUserTokenInfoResponse user = userClient.getUserTokenInfoByOauth(request).getBody().getData();

		if (Objects.isNull(user)) {
			throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
		}

		if ("탈퇴".equals(user.statusName())) {
			throw new UnauthorizedException("탈퇴한 사용자입니다. 관리자에게 문의해 주세요.");
		}

		Long userId = user.userId();
		List<String> roles = user.roles();

		String accessToken = jwtUtils.generateAccessToken(userId, roles, accessTokenExpiresIn);
		String refreshToken = jwtUtils.generateRefreshToken(refreshTokenExpiresIn);

		refreshTokenRepository.deleteByUserId(userId);

		refreshTokenRepository.save(
			new RefreshToken(refreshToken.replace("Bearer ", ""), userId, roles, refreshTokenExpiresIn));

		userClient.updateLastLoginTime(new UpdateLastLoginTimeRequest(userId, LocalDateTime.now()));

		// 클라이언트로 리다이렉트 (토큰 포함)
		String redirectUrl = "https://www.eventor.store/auth/oauth2/login";
		String urlWithTokens = String.format("%s?accessToken=%s&refreshToken=%s",
			redirectUrl,
			URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
			URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));

		try {
			response.sendRedirect(urlWithTokens);
		} catch (IOException e) {
			throw new ServerException();
		}

	}

}
