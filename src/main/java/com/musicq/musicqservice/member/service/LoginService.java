package com.musicq.musicqservice.member.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.LoginDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public interface LoginService {
	ResponseEntity<ResponseDto> login(LoginDto loginDto, HttpServletRequest request);

	ResponseEntity<ResponseDto> autoLogin(HttpServletRequest request);

	ResponseEntity<ResponseDto> logout(HttpServletRequest request, HttpServletResponse response);

	String checkPassword(String id);

	ResponseEntity<String> checkId(String id);

	String chkTokenInCookie(HttpServletRequest request);

	String setCookieToken(String accessToken);

	boolean saveToken(String id, String accessToken);

	String getToken(String id);
}
