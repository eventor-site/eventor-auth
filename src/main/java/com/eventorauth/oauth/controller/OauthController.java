package com.eventorauth.oauth.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventorauth.auth.dto.request.SignUpRequest;
import com.eventorauth.oauth.dto.Oauth2Dto;
import com.eventorauth.oauth.service.OauthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OauthController {
	private final OauthService oauthService;

	@GetMapping("/oauth2/authorization/{registrationId}")
	public ResponseEntity<String> authentication(@PathVariable String registrationId) {
		return ResponseEntity.ok(oauthService.authentication(registrationId));
	}

	@GetMapping("/oauth2/code/{registrationId}")
	public String getToken(@PathVariable String registrationId, @RequestParam String code,
		RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
		SignUpRequest request = oauthService.getToken(registrationId, code);
		Oauth2Dto getOauth2Response = oauthService.getOauth2ByIdentifier(request.identifier());

		if (getOauth2Response != null && getOauth2Response.oauthId() != null) {
			// 아이디도 있고 연동도 된경우 바로 로그인
			oauthService.oauthLogin(request.identifier(), response);

		} else if (getOauth2Response != null) {
			// 아이디는 있는데 연동 안된 경우 oauthId 값 추가 후 로그인
			Oauth2Dto oauth2Dto = new Oauth2Dto(request.identifier(), request.oauthId(), request.oauthType());
			oauthService.oauthConnection(oauth2Dto);
			oauthService.oauthLogin(request.identifier(), response);
		} else {
			// 이메일로 회원 가입된 아이디가 없는 경우
			redirectAttributes.addFlashAttribute("request", request);

			// nickname 입력 페이지로 리다이렉트
			return "redirect:/oauth2/signup";
		}
		return null;
	}

	@PostMapping("/oauth2/signup")
	public void oauthLogin(@ModelAttribute SignUpRequest request,
		HttpServletResponse response) throws IOException {
		oauthService.oauthSignup(request);
		oauthService.oauthLogin(request.identifier(), response);
	}

}
