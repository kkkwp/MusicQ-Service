package com.musicq.musicqservice.member.service;

import com.musicq.musicqservice.member.dto.MemberSignUpDto;
import com.musicq.musicqservice.member.util.Encoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
	private final RestTemplate restTemplate;

	// 회원 가입
	@Override
	public ResponseEntity<String> signup(MemberSignUpDto memberSignUpDto) {
		memberSignUpDto.setPassword(Encoder.encodeStr(memberSignUpDto.getPassword()));
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:81/v1/members/member",
			memberSignUpDto, String.class);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}

	// 회원 정보 조회
	@Override
	public ResponseEntity<String> memberInfoCheck(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/member/{id}",
			String.class, id);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	// 회원 탈퇴
	@Override
	public ResponseEntity<String> unregister(String id) {
		// restTemplate의 delete 메서드는 반환값이 없기때문에
		// exchange로 delete해서 반환값을 얻어온다.
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:81/v1/members/member/{id}",
			HttpMethod.DELETE, HttpEntity.EMPTY, String.class, id);

		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	// id 존재 여부
	@Override
	public ResponseEntity<String> checkId(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/id/{id}",
			String.class, id);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}

	// email 존재 여부
	@Override
	public ResponseEntity<String> checkEmail(String email) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/email/{email}",
			String.class, email);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}

	// nickname 존재여부
	@Override
	public ResponseEntity<String> checkNickname(String nickname) {
		ResponseEntity<String> response = restTemplate.getForEntity(
			"http://localhost:81/v1/members/nickname/{nickname}", String.class, nickname);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}
}
