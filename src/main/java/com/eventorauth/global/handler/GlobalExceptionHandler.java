package com.eventorauth.global.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eventorauth.global.exception.GlobalException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<String> handleGlobalException(GlobalException e) {
		return ResponseEntity.status(e.getErrorStatus().getStatus()).body(e.getMessage());
	}
}