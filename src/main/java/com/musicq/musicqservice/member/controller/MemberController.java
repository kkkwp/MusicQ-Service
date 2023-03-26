package com.musicq.musicqservice.member.controller;

import com.musicq.musicqservice.member.dto.MemberSignUpDto;
import com.musicq.musicqservice.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;


    // 회원 가입
    @PostMapping("/member")
    public ResponseEntity<String> signup(
            @Valid @RequestBody MemberSignUpDto memberInfo
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

    // 회원 탈퇴

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
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<String> checkNickname(
            @Valid @PathVariable("nickname") String nickname
    ){
        return memberService.checkNickname(nickname);
    }
}
