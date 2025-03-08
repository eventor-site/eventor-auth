package com.eventorauth.oauth.service;

import com.eventorauth.auth.dto.request.CheckNicknameRequest;
import com.eventorauth.global.dto.ApiResponse;

public interface UserService {

	ApiResponse<Void> checkNickname(CheckNicknameRequest request);

}
