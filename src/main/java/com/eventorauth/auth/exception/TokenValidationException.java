package com.eventorauth.auth.exception;

import com.eventorauth.global.exception.UnauthorizedException;

public class TokenValidationException extends UnauthorizedException {
	public TokenValidationException(String message) {
		super(message);
	}
}
