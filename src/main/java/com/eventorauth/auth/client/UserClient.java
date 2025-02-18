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

@FeignClient(name = "user-client", url = "${feignClient.url}")
public interface UserClient {

	@GetMapping("/back/users/info")
	GetUserTokenInfoResponse getUserTokenInfoByIdentifier(@RequestParam String identifier);

	@PostMapping("/back/users/oauth2/info")
	GetUserTokenInfoResponse getUserTokenInfoByOauth(@RequestBody OauthDto request);

	@PostMapping("/back/users/signup")
	ResponseEntity<Void> oauthSignup(@RequestBody SignUpRequest request);

	@PostMapping("/back/users/signup/checkIdentifier")
	ResponseEntity<String> checkIdentifier(@RequestBody CheckIdentifierRequest request);

	@PostMapping("/back/signup/oauth2/exists")
	ResponseEntity<Boolean> existsByOauth(@RequestBody OauthDto request);

	@PostMapping("/back/signup/checkNickname")
	ResponseEntity<String> checkNickname(@RequestBody CheckNicknameRequest request);

	@PutMapping("/back/me/lastLoginTime")
	ResponseEntity<Void> updateLastLoginTime(UpdateLastLoginTimeRequest request);
}
