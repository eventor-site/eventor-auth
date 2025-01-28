package com.eventorauth.oauth.dto;

import lombok.Builder;

@Builder
public record OauthDto(
	String oauthId,
	String oauthType
) {
}
