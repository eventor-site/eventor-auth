package com.eventorauth.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {
	private final String oauthId;
	private final String email;
	private final String name;
	private final String birth;
	private final String gender;
	private final String phone;

	@Builder
	public UserProfile(String oauthId, String email, String name, String birth, String gender, String phone) {
		this.oauthId = oauthId;
		this.email = email;
		this.name = name;
		this.birth = birth;
		this.gender = gender;
		this.phone = phone;
	}
}
