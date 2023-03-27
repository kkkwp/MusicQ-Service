package com.musicq.musicqservice.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicq.musicqservice.member.dto.LoginDto;
import com.musicq.musicqservice.member.dto.LoginResDto;
import com.musicq.musicqservice.member.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class LoginController {
	private final LoginService loginService;

	// 로컬 로그인
	@PostMapping("/login")
	public ResponseEntity<LoginResDto> login(
		@Valid @RequestBody LoginDto loginDto,
		HttpServletRequest request
	) {
		// 로그인 결과
		ResponseEntity<LoginResDto> loginResult = loginService.login(loginDto, request);

		return loginResult;
	}

	// 자동 로그인
	@GetMapping("/token")
	public ResponseEntity<LoginResDto> autoLogin(HttpServletRequest request) {
		// 로그인 결과
		ResponseEntity<LoginResDto> loginResult = loginService.autoLogin(request);

		return loginResult;
	}


	// 자동 로그인

	// OAuth 로그인
}
