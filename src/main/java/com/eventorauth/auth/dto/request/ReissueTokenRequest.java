package com.eventorauth.auth.dto.request;

import lombok.Builder;

@Builder
public record ReissueTokenRequest(
	String refreshToken) {
}
