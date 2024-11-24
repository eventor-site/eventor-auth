package com.eventorauth.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventorauth.auth.dto.response.GetUserTokenInfoResponse;

@FeignClient(name = "UserInfoService", url = "http://localhost:8083")
public interface UserInfoClient {
	@GetMapping("/back/users/info")
	GetUserTokenInfoResponse getUserTokenInfoByIdentifier(@RequestParam String identifier);
}
