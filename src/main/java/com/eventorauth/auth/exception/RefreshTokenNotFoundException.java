package com.eventorauth.auth.exception;

import com.eventorauth.global.exception.UnauthorizedException;

public class RefreshTokenNotFoundException extends UnauthorizedException {
	public RefreshTokenNotFoundException() {
		super("재발급 토큰을 찾을 수 없습니다.");
	}
}
