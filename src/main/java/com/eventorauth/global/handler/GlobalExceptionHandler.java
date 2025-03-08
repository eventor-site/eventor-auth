package com.eventorauth.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eventorauth.global.dto.ApiResponse;
import com.eventorauth.global.exception.GlobalException;
import com.eventorauth.global.exception.payload.ErrorStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiResponse<Void>> handleGlobalException(GlobalException e) {
		ErrorStatus errorStatus = e.getErrorStatus();
		return ApiResponse.createError(errorStatus.getStatus(), errorStatus.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception e) {
		return ApiResponse.createError(HttpStatus.INTERNAL_SERVER_ERROR, "인증 서버 오류");
	}
}