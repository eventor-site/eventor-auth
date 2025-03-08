package com.eventorauth.oauth.service;

import org.springframework.stereotype.Service;

import com.eventorauth.auth.client.UserClient;
import com.eventorauth.auth.dto.request.CheckNicknameRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserClient userClient;

	public String checkNickname(CheckNicknameRequest request) {
		return userClient.checkNickname(request).getMessage();
	}
}
