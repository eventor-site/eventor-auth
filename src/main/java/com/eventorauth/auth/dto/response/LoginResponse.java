package com.eventorauth.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
	String accessToken,
	String refreshToken,
	String userStatusName
) {
}
