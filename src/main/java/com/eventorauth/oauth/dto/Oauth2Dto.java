package com.eventorauth.oauth.dto;

import lombok.Builder;

@Builder
public record Oauth2Dto(
	String identifier,
	String oauthId,
	String oauthType
) {
}
