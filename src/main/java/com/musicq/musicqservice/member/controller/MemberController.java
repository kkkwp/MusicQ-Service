package com.musicq.musicqservice.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.musicq.musicqservice.member.dto.MemberInfoDto;
import com.musicq.musicqservice.member.dto.ResultResDto;
import com.musicq.musicqservice.member.service.MailService;
import com.musicq.musicqservice.member.service.MemberService;
import com.musicq.musicqservice.member.util.UploaderLocal;
import com.musicq.musicqservice.member.util.UploderS3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
	private final MemberService memberService;
	private final UploaderLocal uploaderLocal;
	private final UploderS3 uploderS3;
	private final MailService mailService;

	// 회원 가입
	@PostMapping("/member")
	public ResponseEntity<String> signup(
		@Valid @RequestBody MemberInfoDto memberInfo
	) {
		return memberService.signup(memberInfo);
	}

	// 회원 정보 조회
	@GetMapping("/member/{id}")
	public ResponseEntity<String> memberInfoCheck(
		@Valid @PathVariable("id") String id
	) {
		return memberService.memberInfoCheck(id);
	}

	// 회원 정보 수정
	@PutMapping("/member/{id}")
	public ResponseEntity<Object> memberInfoChanges(
		@Valid @PathVariable("id") String id,
		@Valid @RequestBody MemberInfoDto memberInfoDto
	) {
		return memberService.memberInfoChange(id, memberInfoDto);
	}

	// 회원 탈퇴
	@DeleteMapping("/member/{id}")
	public ResponseEntity<String> unregister(
		@Valid @PathVariable("id") String id
	) {
		return memberService.unregister(id);
	}

	// 이미지 업로드 to local
	@PostMapping("/member/upload/local")
	public ResponseEntity<?> uploadToLocal(
		@Valid MultipartFile[] uploadFiles
	) {
		return uploaderLocal.uploadToLocal(uploadFiles);
	}

	// 이미지 업로드 to S3
	@PostMapping("member/upload/S3")
	public ResponseEntity<?> uploadToS3(
		@Valid MultipartFile uploadFile
	) {
		return uploderS3.uploadToS3(uploadFile);
	}

	// ID 존재 여부 - 회원 가입 때 중복확인
	@GetMapping("/id/{id}")
	public ResponseEntity<String> checkId(
		@Valid @PathVariable("id") String id
	) {
		return memberService.checkId(id);
	}

	// email 존재 여부 - email 중복확인
	@GetMapping("/email/{email}")
	public ResponseEntity<String> checkEmail(
		@Valid @PathVariable("email") String email
	) {
		return memberService.checkEmail(email);
	}

	// Nickname 존재 여부 - nickname 중복 확인
	@GetMapping("/nickname/{id}/{nickname}")
	public ResponseEntity<String> checkNickname(
		@Valid @PathVariable("id") String id,
		@Valid @PathVariable("nickname") String nickname
	) {
		return memberService.checkNickname(id, nickname);
	}

	@GetMapping("/email/authentication/{email}")
	public ResponseEntity<ResultResDto> emailAuthentication(
		@Valid @PathVariable("email") String email
	) {
		// 리퍼럴 추적 실패 메서드, 나중에 강사님한테 물어보고 해결책 찾으면 다시 살릴 수 도 있어서 주석 처리
		//mailService.sendMail1(email);
		return mailService.sendMail(email);
	}

	@GetMapping("/email/authentication/referer")
	public ResponseEntity<ResultResDto> checkReferer(
		@Valid HttpServletRequest request
	) {
		return mailService.checkReferer(request);
	}
}