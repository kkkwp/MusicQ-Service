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


    @PostMapping("/member")
    public ResponseEntity<String> signup(
            @Valid @RequestBody MemberSignUpDto memberInfo
    ){
        return memberService.signup(memberInfo);
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<String> checkId(
            @Valid @PathVariable("id") String id
    ){
        return memberService.checkId(id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<String> checkEmail(
            @Valid @PathVariable("email") String email
    ){
        return memberService.checkEmail(email);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<String> checkNickname(
            @Valid @PathVariable("nickname") String nickname
    ){
        return memberService.checkNickname(nickname);
    }
}
