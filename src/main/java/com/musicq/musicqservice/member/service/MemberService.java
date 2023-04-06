package com.musicq.musicqservice.member.service;

import com.musicq.musicqservice.member.dto.MemberInfoDto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
	ResponseEntity<String> signup(MemberInfoDto memberInfoDto);

	ResponseEntity<String> memberInfoCheck(String id);

	ResponseEntity<Object> memberInfoChange(String id, MemberInfoDto memberInfoDto);

	ResponseEntity<String> unregister(String id);

	ResponseEntity<String> checkId(String id);

	ResponseEntity<String> checkEmail(String email);

	ResponseEntity<String> checkNickname(String id, String nickname);
}
