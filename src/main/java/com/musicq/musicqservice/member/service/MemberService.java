package com.musicq.musicqservice.service;

import com.musicq.musicqservice.dto.MemberSignUpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface MemberService {
    ResponseEntity<String> signup(MemberSignUpDto memberSignUpDto);
    ResponseEntity<String> checkId(String id);
    ResponseEntity<String> checkEmail(String email);
    ResponseEntity<String> checkNickname(String nickname);
}
