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
import com.eventorauth.oauth.dto.Oauth2Dto;

@FeignClient(name = "user-client", url = "http://localhost:8083/back/users")
public interface UserClient {

	@GetMapping("/info")
	GetUserTokenInfoResponse getUserTokenInfoByIdentifier(@RequestParam String identifier);

	@PostMapping("/signup")
	ResponseEntity<Void> oauthSignup(@RequestBody SignUpRequest request);

	@PostMapping("/signup/checkIdentifier")
	ResponseEntity<String> checkIdentifier(@RequestBody CheckIdentifierRequest request);

	@GetMapping("/signup/oauth2/identifier")
	ResponseEntity<Oauth2Dto> getOauth2ByIdentifier(@RequestParam String identifier);

	@PostMapping("/signup/oauth2/connection")
	ResponseEntity<Oauth2Dto> oauth2Connection(@RequestBody Oauth2Dto dto);

	@PostMapping("/signup/checkNickname")
	ResponseEntity<String> checkNickname(@RequestBody CheckNicknameRequest request);

	@PutMapping("/me/lastLoginTime")
	ResponseEntity<Void> updateLastLoginTime(UpdateLastLoginTimeRequest request);
}
