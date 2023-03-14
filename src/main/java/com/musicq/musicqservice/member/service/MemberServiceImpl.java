package com.musicq.musicqservice.member.service;

import com.musicq.musicqservice.member.dto.MemberSignUpDto;
import com.musicq.musicqservice.member.util.Encoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> signup(MemberSignUpDto memberSignUpDto) {
        memberSignUpDto.setPassword(Encoder.encodeStr(memberSignUpDto.getPassword()));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:81/v1/members/signup",memberSignUpDto ,String.class);
        log.info(response.getStatusCode());
        log.info(response.getHeaders());
        log.info(response.getBody());
        return response;
    }


    @Override
    public ResponseEntity<String> checkId(String id) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/id/{id}", String.class, id);
        log.info(response.getStatusCode());
        log.info(response.getHeaders());
        log.info(response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<String> checkEmail(String email) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/email/{email}", String.class, email);
        log.info(response.getStatusCode());
        log.info(response.getHeaders());
        log.info(response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<String> checkNickname(String nickname) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/nickname/{nickname}", String.class, nickname);
        log.info(response.getStatusCode());
        log.info(response.getHeaders());
        log.info(response.getBody());
        return response;
    }
}
