package com.eventorauth.auth.dto.request;

import java.time.LocalDateTime;

public record UpdateLastLoginTimeRequest(
	Long userId,
	LocalDateTime lastLoginTime) {
}
