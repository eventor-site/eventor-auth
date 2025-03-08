package com.eventorauth.oauth.dto;

import lombok.Builder;

@Builder
public record OauthRedirectUrlResponse(
	String oauthRedirectUrl
) {
}
