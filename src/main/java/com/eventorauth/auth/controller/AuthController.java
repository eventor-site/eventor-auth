package com.eventorauth.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorauth.auth.dto.request.ReissueTokenRequest;
import com.eventorauth.auth.dto.response.ReissueTokensResponse;
import com.eventorauth.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	/**
	 * 토큰 재발급
	 */
	@PostMapping("/reissue-token")
	public ResponseEntity<ReissueTokensResponse> reissueTokens(
		@RequestBody ReissueTokenRequest request) {
		ReissueTokensResponse reissuedTokens = authService.reissueTokens(request);

		if (reissuedTokens == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(reissuedTokens);
	}
}
