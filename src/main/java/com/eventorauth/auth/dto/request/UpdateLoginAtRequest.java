package com.eventorauth.auth.dto.request;

import java.time.LocalDateTime;

public record UpdateLoginAtRequest(
	Long userId,
	LocalDateTime loginAt) {
}
