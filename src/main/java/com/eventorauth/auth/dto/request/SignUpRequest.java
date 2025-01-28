package com.eventorauth.auth.dto.request;

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
	String oauthId,
	String oauthType
) {
	public static SignUpRequest fromUserProfile(UserProfile userProfile) {
		return SignUpRequest.builder()
			// .identifier(userProfile.getEmail())
			// .password(UUID.randomUUID().toString())
			// .name(userProfile.getName())
			.email(userProfile.email())
			// .birth(userProfile.getBirth())
			// .gender(userProfile.getGender())
			// .phone(userProfile.getPhone())
			.oauthId(userProfile.oauthId())
			.oauthType(userProfile.oauthType())
			.build();
	}
}

