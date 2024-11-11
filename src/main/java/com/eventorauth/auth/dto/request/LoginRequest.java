package com.eventorauth.auth.dto.request;

public record LoginRequest(
	String id,
	String password) {
}
