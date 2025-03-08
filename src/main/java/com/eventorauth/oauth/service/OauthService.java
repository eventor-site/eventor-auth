package com.eventorauth.oauth.service;

import java.io.IOException;
import java.util.Map;

import com.eventorauth.auth.dto.request.SignUpRequest;
import com.eventorauth.global.dto.ApiResponse;
import com.eventorauth.oauth.dto.OauthDto;
import com.eventorauth.oauth.dto.OauthTokenResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface OauthService {

	String authentication(String registrationId);

	SignUpRequest getToken(String registrationId, String code);

	Map<String, Object> getUserAttributes(String registrationId, OauthTokenResponse tokenResponse);

	ApiResponse<Boolean> existsByOauth(OauthDto request);

	void oauthSignup(SignUpRequest request);

	void oauthLogin(OauthDto request, HttpServletResponse response) throws IOException;

}