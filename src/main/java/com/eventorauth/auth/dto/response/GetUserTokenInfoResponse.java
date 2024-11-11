package com.eventorauth.auth.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record GetUserTokenInfoResponse(
	Long userId,
	String password,
	List<String> roles,
	String statusName
) {
}
