package com.eventorauth.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorauth.auth.dto.ReissueTokenDto;
import com.eventorauth.auth.service.AuthService;

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
	public ResponseEntity<ReissueTokenDto> reissueTokens(
		@RequestBody ReissueTokenDto request) {
		ReissueTokenDto reissuedTokens = authService.reissueTokens(request);

		log.info("재발급 토큰 Access-Token: {} Refresh-Token: {}", reissuedTokens.accessToken(),
			reissuedTokens.refreshToken());

		if (reissuedTokens.refreshToken() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		return ResponseEntity.status(HttpStatus.OK).body(reissuedTokens);
	}

}
