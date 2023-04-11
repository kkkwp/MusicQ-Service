package com.musicq.musicqservice.member.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.member.dto.MemberInfoChangeDto;
import com.musicq.musicqservice.member.dto.MemberSignUpInfoDto;

@Service
public interface MemberService {
	ResponseEntity<String> signup(MemberSignUpInfoDto memberSignUpInfoDto);

	ResponseEntity<String> memberInfoCheck(String id);

	ResponseEntity<Object> memberInfoChange(String id, MemberInfoChangeDto memberInfoChangeDto);

	ResponseEntity<Object> changPassword(String id, String password);

	ResponseEntity<String> unregister(String id);

	ResponseEntity<String> checkId(String id);

	ResponseEntity<String> checkEmail(String email);

	ResponseEntity<String> checkNickname(String id, String nickname);
}
