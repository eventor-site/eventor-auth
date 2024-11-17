package com.eventorauth.auth.dto.request;

public record LoginRequest(
	String identifier,
	String password) {
}
