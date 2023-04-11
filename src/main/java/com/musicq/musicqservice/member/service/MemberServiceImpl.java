package com.musicq.musicqservice.member.service;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.member.dto.MemberInfoChangeDto;
import com.musicq.musicqservice.member.dto.MemberSignUpInfoDto;
import com.musicq.musicqservice.member.dto.ResultResDto;
import com.musicq.musicqservice.member.util.Encoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
	private final RestTemplate restTemplate;

	// 회원 가입
	@Override
	public ResponseEntity<String> signup(MemberSignUpInfoDto memberSignUpInfoDto) {
		log.warn(memberSignUpInfoDto.getMemberImage().getPath());
		log.warn(memberSignUpInfoDto.getMemberImage().getProfile_img());
		log.warn(memberSignUpInfoDto.getMemberImage().getUuid());
		memberSignUpInfoDto.setPassword(Encoder.encodeStr(memberSignUpInfoDto.getPassword().toLowerCase()));
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:81/v1/members/member",
			memberSignUpInfoDto, String.class);
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

	// 비밀 번호 수정
	@Override
	public ResponseEntity<Object> changPassword(String id, String password) {
		try {
			String EnPassword = Encoder.encodeStr(password.toLowerCase());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(EnPassword, headers);

			ResponseEntity<Object> response = restTemplate.exchange("http://localhost:81/v1/members/password/{id}",
				HttpMethod.PUT, request, Object.class, id);
			return response;

		} catch (NullPointerException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());
			log.warn("React 에서 값 잘못 준거임 ㅅㄱ.");
			ResultResDto failResponse = new ResultResDto("Invalid Variable");

			return ResponseEntity.badRequest().body(failResponse);
		}
	}

	// 회원 정보 수정
	@Override
	public ResponseEntity<Object> memberInfoChange(String id, MemberInfoChangeDto memberInfoChangeDto) {
		ResponseEntity<Object> response = null;
		try {
			JSONObject jsonNicknameInfo = new JSONObject(
				checkNickname(id, memberInfoChangeDto.getNickname()).getBody());
			// DB 에 사용자가 입력한 닉네임의 개수
			String nickNameCount = jsonNicknameInfo.getString("count");

			// 현재 사용자(id 기준)의 DB에 저장된 닉네임
			String currentNickname = jsonNicknameInfo.getString("currentNickname");

			// 닉네임이 중복되는지 React에서도 물어보지만 Spring에 서도 한번 더 유효성 검사
			// count 가 1 이여도 현재 닉네임에서 변경하지 않고 그대로 request를 보내도 본인의 닉네임에 대해서는 허용
			if (nickNameCount.equals("0") || memberInfoChangeDto.getNickname().equals(currentNickname)) {

				// 그냥 restTemplate.put은 반환 값이 없어서 exchange 함수를 사용해서 반환값을 받기 위해서 헤더와 요청 body를 생성
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<MemberInfoChangeDto> request = new HttpEntity<>(memberInfoChangeDto, headers);

				response = restTemplate.exchange("http://localhost:81/v1/members/member/{id}", HttpMethod.PUT,
					request, Object.class, id);
			} else {
				ResultResDto failResponse = new ResultResDto("Exist Nickname");
				log.warn("이 에러 발생 시 React에서 Nickname 중복확인 요청 없이 회원가입 요청을 진행한 것.");
				return ResponseEntity.internalServerError().body(failResponse);
			}

		} catch (NullPointerException | HttpServerErrorException | HttpClientErrorException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());
			log.warn("React 에서 값 잘못 준거임 ㅅㄱ.");
			ResultResDto failResponse = new ResultResDto("Invalid Variable");
			return ResponseEntity.badRequest().body(failResponse);
		}
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
	public ResponseEntity<String> checkNickname(String id, String nickname) {
		ResponseEntity<String> response = restTemplate.getForEntity(
			"http://localhost:81/v1/members/nickname/{id}/{nickname}", String.class, id, nickname);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}
}