package com.musicq.musicqservice.member.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.member.dto.LoginDto;
import com.musicq.musicqservice.member.dto.LoginResDto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface LoginService {
	ResponseEntity<LoginResDto> login(LoginDto loginDto, HttpServletRequest request);
	ResponseEntity<LoginResDto> autoLogin(HttpServletRequest request);

	String checkPassword(String id);
	ResponseEntity<String> checkId(String id);

	String chkTokenInCookie(HttpServletRequest request);
	String setCookieToken(String accessToken);

	boolean saveToken(String id, String accessToken);
	String getToken(String id);
}
