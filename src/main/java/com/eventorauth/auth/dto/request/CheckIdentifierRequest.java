package com.eventorauth.auth.dto.request;

import lombok.Builder;

@Builder
public record CheckIdentifierRequest(
	String identifier
) {
}
