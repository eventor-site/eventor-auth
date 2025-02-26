package com.eventorauth.global.exception;

import com.eventorauth.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	private final ErrorStatus errorStatus;

	public GlobalException(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
		this.errorStatus = errorStatus;
	}
}
