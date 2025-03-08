package com.eventorauth.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorauth.auth.dto.ReissueTokenDto;
import com.eventorauth.auth.service.AuthService;
import com.eventorauth.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	/**
	 * 토큰 재발급
	 */
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<ReissueTokenDto>> reissueTokens(@RequestBody ReissueTokenDto request) {
		return ApiResponse.createSuccess(authService.reissueToken(request));
	}

}
