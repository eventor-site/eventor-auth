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
import com.eventorauth.global.dto.ApiResponse;
import com.eventorauth.oauth.dto.OauthDto;

@FeignClient(name = "user-client", url = "${feignClient.url}")
public interface UserClient {

	@GetMapping("/back/users/info")
	ResponseEntity<ApiResponse<GetUserTokenInfoResponse>> getUserTokenInfoByIdentifier(@RequestParam String identifier);

	@PostMapping("/back/users/oauth2/info")
	ResponseEntity<ApiResponse<GetUserTokenInfoResponse>> getUserTokenInfoByOauth(@RequestBody OauthDto request);

	@PostMapping("/back/users/signup")
	ResponseEntity<ApiResponse<Void>> oauthSignup(@RequestBody SignUpRequest request);

	@PostMapping("/back/users/signup/checkIdentifier")
	ResponseEntity<ApiResponse<Void>> checkIdentifier(@RequestBody CheckIdentifierRequest request);

	@PostMapping("/back/users/signup/oauth2/exists")
	ResponseEntity<ApiResponse<Boolean>> existsByOauth(@RequestBody OauthDto request);

	@PostMapping("/back/users/signup/checkNickname")
	ResponseEntity<ApiResponse<Void>> checkNickname(@RequestBody CheckNicknameRequest request);

	@PutMapping("/back/users/me/lastLoginTime")
	ResponseEntity<ApiResponse<Void>> updateLastLoginTime(UpdateLastLoginTimeRequest request);
}
