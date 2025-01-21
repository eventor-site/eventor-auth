package com.eventorauth.auth.dto.request;

import java.util.UUID;

import com.eventorauth.oauth.dto.UserProfile;

import lombok.Builder;

@Builder
public record SignUpRequest(
	String identifier,
	String password,
	String name,
	String nickname,
	String email,
	String birth,
	String gender,
	String phone,
	String oauthId) {
	public static SignUpRequest fromUserProfile(UserProfile userProfile) {
		return SignUpRequest.builder()
			.oauthId(userProfile.getOauthId())
			.identifier(userProfile.getEmail())
			.password(UUID.randomUUID().toString())
			.name(userProfile.getName())
			.email(userProfile.getEmail())
			.birth(userProfile.getBirth())
			.gender(userProfile.getGender())
			.phone(userProfile.getPhone())
			.build();
	}
}

