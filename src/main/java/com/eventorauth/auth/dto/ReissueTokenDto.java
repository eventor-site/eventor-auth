package com.eventorauth.auth.dto;

import lombok.Builder;

@Builder
public record ReissueTokenDto(
	String accessToken,
	String refreshToken
) {
}
