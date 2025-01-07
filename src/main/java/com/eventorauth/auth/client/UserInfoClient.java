package com.eventorauth.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventorauth.auth.dto.request.UpdateLastLoginTimeRequest;
import com.eventorauth.auth.dto.response.GetUserTokenInfoResponse;

@FeignClient(name = "user", url = "http://localhost:8083/back/users")
public interface UserInfoClient {
	@GetMapping("/info")
	GetUserTokenInfoResponse getUserTokenInfoByIdentifier(@RequestParam String identifier);

	@PutMapping("/me/lastLoginTime")
	ResponseEntity<Void> updateLastLoginTime(UpdateLastLoginTimeRequest request);
}
