package com.eventorauth.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventorauth.auth.dto.request.CheckIdentifierRequest;
import com.eventorauth.auth.dto.request.CheckNicknameRequest;
import com.eventorauth.auth.dto.request.SignUpRequest;
import com.eventorauth.auth.dto.request.UpdateLastLoginTimeRequest;
import com.eventorauth.auth.dto.response.GetUserTokenInfoResponse;
import com.eventorauth.oauth.dto.OauthDto;

@FeignClient(name = "user-client", url = "http://localhost:8083/back/users")
public interface UserClient {

	@GetMapping("/info")
	GetUserTokenInfoResponse getUserTokenInfoByIdentifier(@RequestParam String identifier);

	@PostMapping("/oauth2/info")
	GetUserTokenInfoResponse getUserTokenInfoByOauth(@RequestBody OauthDto request);

	@PostMapping("/signup")
	ResponseEntity<Void> oauthSignup(@RequestBody SignUpRequest request);

	@PostMapping("/signup/checkIdentifier")
	ResponseEntity<String> checkIdentifier(@RequestBody CheckIdentifierRequest request);

	@PostMapping("/signup/oauth2/exists")
	ResponseEntity<Boolean> existsByOauth(@RequestBody OauthDto request);

	@PostMapping("/signup/checkNickname")
	ResponseEntity<String> checkNickname(@RequestBody CheckNicknameRequest request);

	@PutMapping("/me/lastLoginTime")
	ResponseEntity<Void> updateLastLoginTime(UpdateLastLoginTimeRequest request);
}
