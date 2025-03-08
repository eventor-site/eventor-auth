package com.eventorauth.oauth.service.impl;

import org.springframework.stereotype.Service;

import com.eventorauth.auth.client.UserClient;
import com.eventorauth.auth.dto.request.CheckNicknameRequest;
import com.eventorauth.global.dto.ApiResponse;
import com.eventorauth.oauth.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserClient userClient;

	public ApiResponse<Void> checkNickname(CheckNicknameRequest request) {
		return userClient.checkNickname(request).getBody();
	}
}
