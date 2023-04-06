package com.musicq.musicqservice.member.controller;

import com.musicq.musicqservice.member.dto.MemberInfoDto;
import com.musicq.musicqservice.member.service.MemberService;
import com.musicq.musicqservice.member.util.UploaderLocal;
import com.musicq.musicqservice.member.util.UploderS3;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final UploaderLocal uploaderLocal;
    private final UploderS3 uploderS3;

    // 회원 가입
    @PostMapping("/member")
    public ResponseEntity<String> signup(
            @Valid @RequestBody MemberInfoDto memberInfo
    ){
        return memberService.signup(memberInfo);
    }

    // 회원 정보 조회
    @GetMapping("/member/{id}")
    public ResponseEntity<String> memberInfoCheck(
        @Valid @PathVariable("id") String id
    ){
        return memberService.memberInfoCheck(id);
    }

    // 회원 정보 수정
    @PutMapping("/member/{id}")
    public ResponseEntity<Object> memberInfoChanges(
        @Valid @PathVariable("id") String id,
        @Valid @RequestBody MemberInfoDto memberInfoDto
    ){
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
    ){
        return uploaderLocal.uploadToLocal(uploadFiles);
    }

    // 이미지 업로드 to S3
    @PostMapping("member/upload/S3")
    public ResponseEntity<?> uploadToS3(
        @Valid MultipartFile uploadFile
    ){
        return uploderS3.uploadToS3(uploadFile);
    }

    // ID 존재 여부 - 회원 가입 때 중복확인
    @GetMapping("/id/{id}")
    public ResponseEntity<String> checkId(
            @Valid @PathVariable("id") String id
    ){
        return memberService.checkId(id);
    }

    // email 존재 여부 - email 중복확인
    @GetMapping("/email/{email}")
    public ResponseEntity<String> checkEmail(
            @Valid @PathVariable("email") String email
    ){
        return memberService.checkEmail(email);
    }

    // Nickname 존재 여부 - nickname 중복 확인
    @GetMapping("/nickname/{id}/{nickname}")
    public ResponseEntity<String> checkNickname(
            @Valid @PathVariable("id") String id,
            @Valid @PathVariable("nickname") String nickname
    ){
        return memberService.checkNickname(id, nickname);
    }
}
