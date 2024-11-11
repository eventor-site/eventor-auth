package com.eventorauth.auth.dto.response;

import lombok.Builder;

@Builder
public record ReissueTokensResponse(
	String accessToken,
	String refreshToken
) {
}
