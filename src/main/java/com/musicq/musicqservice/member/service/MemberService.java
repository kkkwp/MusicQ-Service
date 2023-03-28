package com.musicq.musicqservice.member.service;

import com.musicq.musicqservice.member.dto.LoginDto;
import com.musicq.musicqservice.member.dto.MemberSignUpDto;
import com.musicq.musicqservice.member.dto.TokenDto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
	ResponseEntity<String> signup(MemberSignUpDto memberSignUpDto);

	ResponseEntity<String> memberInfoCheck(String id);

	ResponseEntity<String> unregister(String id);

	ResponseEntity<String> checkId(String id);

	ResponseEntity<String> checkEmail(String email);

	ResponseEntity<String> checkNickname(String nickname);
}
