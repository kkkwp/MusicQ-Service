package com.musicq.musicqservice.member.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.ChangePwDto;
import com.musicq.musicqservice.member.dto.MemberInfoChangeDto;
import com.musicq.musicqservice.member.dto.MemberSignUpInfoDto;

@Service
public interface MemberService {
	ResponseEntity<ResponseDto> signup(MemberSignUpInfoDto memberSignUpInfoDto);

	ResponseEntity<ResponseDto> memberInfoCheck(String id);

	ResponseEntity<ResponseDto> memberInfoChange(String id, MemberInfoChangeDto memberInfoChangeDto);

	String checkPassword(String id);

	ResponseEntity<ResponseDto> changePassword(String id, ChangePwDto changePwDto);

	ResponseEntity<ResponseDto> unregister(String id);

	ResponseEntity<ResponseDto> checkId(String id);

	ResponseEntity<ResponseDto> checkEmail(String email);

	ResponseEntity<ResponseDto> checkNickname(String id, String nickname);
}
