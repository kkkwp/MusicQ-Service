package com.musicq.musicqservice.member.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.ResultResDto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface MailService {
	ResponseEntity<ResultResDto> sendMail1(String email);

	ResponseEntity<ResponseDto> sendMail(String email);

	ResponseEntity<ResultResDto> checkReferer(HttpServletRequest request);

	String createAuthKey();
}