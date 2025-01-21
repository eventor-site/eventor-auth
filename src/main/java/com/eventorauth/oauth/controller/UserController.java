package com.eventorauth.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.eventorauth.auth.dto.request.CheckNicknameRequest;
import com.eventorauth.auth.dto.request.SignUpRequest;
import com.eventorauth.oauth.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/oauth2/signup")
	public String nickname(@ModelAttribute("request") SignUpRequest request, Model model) {
		model.addAttribute("request", request); // request 정보를 모델에 추가하여 뷰에서 사용할 수 있게 함
		return "oauth/nickname";
	}

	@PostMapping("/oauth2/signup/checkNickname")
	public ResponseEntity<String> checkNickname(@ModelAttribute CheckNicknameRequest request) {
		return userService.checkNickname(request);
	}
}
