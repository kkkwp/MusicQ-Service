package com.musicq.musicqservice.member.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.common.Exception.ErrorCode;
import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.ChangePwDto;
import com.musicq.musicqservice.member.dto.MemberInfoChangeDto;
import com.musicq.musicqservice.member.dto.MemberSignUpInfoDto;
import com.musicq.musicqservice.member.util.Encoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
	private final RestTemplate restTemplate;

	// 회원 가입

	@Value("${domainApplication.url}")
	private String domainUrl;

	@Override
	public ResponseEntity<ResponseDto> signup(MemberSignUpInfoDto memberSignUpInfoDto) {
		try {
			JSONObject jsonExistId = new JSONObject(checkId(memberSignUpInfoDto.getId()).getBody());
			JSONObject jsonExistEmail = new JSONObject(checkEmail(memberSignUpInfoDto.getEmail()).getBody());
			JSONObject jsonExistNickName = new JSONObject(checkNickname(memberSignUpInfoDto.getId(),
				memberSignUpInfoDto.getNickname()).getBody());

			long cntExistId = jsonExistId.getJSONObject("data").getLong("count");
			long cntExistEmail = jsonExistEmail.getJSONObject("data").getLong("count");
			long cntExistNickName = Long.parseLong(jsonExistNickName.getJSONObject("data").getString("count"));

			if (cntExistId + cntExistEmail + cntExistNickName == 0) {
				memberSignUpInfoDto.setPassword(Encoder.encodeStr(memberSignUpInfoDto.getPassword().toLowerCase()));
				ResponseEntity<Object> result = restTemplate.postForEntity(domainUrl + "members/member",
					memberSignUpInfoDto, Object.class);

				ResponseDto response = ResponseDto.builder()
					.success(true)
					.data(result.getBody())
					.build();
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (cntExistId != 0) {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.DUPLICATE_ID)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else if (cntExistEmail != 0) {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.DUPLICATE_EMAIL)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else if (cntExistNickName != 0) {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.DUPLICATE_NICKNAME)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.INVALID_INPUT_VALUE)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.INTERNAL_SERVER_ERROR)
				.build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 회원 정보 조회
	@Override
	public ResponseEntity<ResponseDto> memberInfoCheck(String id) {
		try {
			JSONObject jsonIdExist = new JSONObject(checkId(id).getBody());
			log.warn(jsonIdExist);
			if (jsonIdExist.getJSONObject("data").getLong("count") == 1) {
				ResponseEntity<Object> result = restTemplate.getForEntity(domainUrl + "members/member/{id}",
					Object.class, id);

				ResponseDto response = ResponseDto.builder()
					.success(true)
					.data(result.getBody())
					.build();
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.INVALID_INPUT_VALUE)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.INTERNAL_SERVER_ERROR)
				.build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 비밀 번호 수정
	@Override
	public ResponseEntity<ResponseDto> changePassword(String id, ChangePwDto changePwDto) {
		try {
			String currentUserPassword = checkPassword(id);
			if (Encoder.isMatch(changePwDto.getCurPassword(), currentUserPassword)) {
				if (changePwDto.getChangedPassword().equals(changePwDto.getChkChangedPassword())) {
					String changePassword = Encoder.encodeStr(changePwDto.getChkChangedPassword().toLowerCase());

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<String> request = new HttpEntity<>(changePassword, headers);

					ResponseEntity<Object> result = restTemplate.exchange(
						domainUrl + "members/member/password/{id}", HttpMethod.PUT, request, Object.class,
						id);

					ResponseDto response = ResponseDto.builder().success(true).data(result.getBody()).build();
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					ResponseDto response = ResponseDto.builder()
						.success(false)
						.error(ErrorCode.NOT_EQUALS_INPUT_CHANGED_PW)
						.build();
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
			} else {
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.NOT_EQUALS_INPUT_CURRENT_PW)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INVALID_INPUT_VALUE).build();
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// 회원 정보 수정
	@Override
	public ResponseEntity<ResponseDto> memberInfoChange(String id, MemberInfoChangeDto memberInfoChangeDto) {
		try {
			JSONObject jsonNicknameInfo = new JSONObject(
				checkNickname(id, memberInfoChangeDto.getNickname()).getBody());

			log.warn(jsonNicknameInfo);
			if (jsonNicknameInfo.getBoolean("success")) {
				// DB 에 사용자가 입력한 닉네임의 개수
				String nickNameCount = jsonNicknameInfo.getJSONObject("data").getString("count");

				// 현재 사용자(id 기준)의 DB에 저장된 닉네임
				String currentNickname = jsonNicknameInfo.getJSONObject("data").getString("currentNickname");

				log.warn(nickNameCount, currentNickname);
				// 닉네임이 중복되는지 React에서도 물어보지만 Spring에 서도 한번 더 유효성 검사
				// count 가 1 이여도 현재 닉네임에서 변경하지 않고 그대로 request를 보내도 본인의 닉네임에 대해서는 허용
				if (nickNameCount.equals("0") || memberInfoChangeDto.getNickname().equals(currentNickname)) {

					// 그냥 restTemplate.put은 반환 값이 없어서 exchange 함수를 사용해서 반환값을 받기 위해서 헤더와 요청 body를 생성
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<MemberInfoChangeDto> request = new HttpEntity<>(memberInfoChangeDto, headers);

					ResponseEntity<Object> result = restTemplate.exchange(domainUrl + "members/member/{id}",
						HttpMethod.PUT, request, Object.class, id);
					log.info(result.getStatusCode());
					log.info(result.getHeaders());
					log.info(result.getBody());

					ResponseDto response = ResponseDto.builder().success(true).data(result.getBody()).build();
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					ResponseDto response = ResponseDto.builder()
						.success(false)
						.error(ErrorCode.INTERNAL_SERVER_ERROR)
						.build();
					return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.DUPLICATE_NICKNAME).build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INVALID_INPUT_VALUE).build();
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// 회원 탈퇴
	@Override
	public ResponseEntity<ResponseDto> unregister(String id) {
		try {
			// restTemplate의 delete 메서드는 반환값이 없기때문에
			// exchange로 delete해서 반환값을 얻어온다.
			ResponseEntity<String> result = restTemplate.exchange(domainUrl + "members/member/{id}",
				HttpMethod.DELETE, HttpEntity.EMPTY, String.class, id);

			log.info(result.getStatusCode());
			log.info(result.getHeaders());
			log.info(result.getBody());

			JSONObject jsonResponse = new JSONObject(result.getBody());

			if (jsonResponse.getBoolean("result") == true) {
				Map<String, Boolean> resData = new HashMap<>();
				resData.put("result", jsonResponse.getBoolean("result"));
				ResponseDto response = ResponseDto.builder().success(true).data(resData).build();
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.NOT_EXIST_ID).build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INTERNAL_SERVER_ERROR).build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// id 존재 여부
	@Override
	public ResponseEntity<ResponseDto> checkId(String id) {
		try {
			ResponseEntity<String> result = restTemplate.getForEntity(domainUrl + "members/id/{id}",
				String.class, id);
			log.info(result.getStatusCode());
			log.info(result.getHeaders());
			log.info(result.getBody());

			JSONObject jsonResponse = new JSONObject(result.getBody());

			if (jsonResponse.getLong("count") == 0) {
				Map<String, Long> resData = new HashMap<>();
				resData.put("count", jsonResponse.getLong("count"));

				ResponseDto response = ResponseDto.builder().success(true).data(resData).build();
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				Map<String, Long> resData = new HashMap<>();
				resData.put("count", jsonResponse.getLong("count"));
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.data(resData)
					.error(ErrorCode.DUPLICATE_ID)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INTERNAL_SERVER_ERROR).build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// email 존재 여부
	@Override
	public ResponseEntity<ResponseDto> checkEmail(String email) {
		try {
			ResponseEntity<String> result = restTemplate.getForEntity(domainUrl + "members/email/{email}",
				String.class, email);
			log.info(result.getStatusCode());
			log.info(result.getHeaders());
			log.info(result.getBody());

			JSONObject jsonResponse = new JSONObject(result.getBody());

			if (jsonResponse.getLong("count") == 0) {

				Map<String, Long> resData = new HashMap<>();
				resData.put("count", jsonResponse.getLong("count"));

				ResponseDto response = ResponseDto.builder().success(true).data(resData).build();
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				Map<String, Long> resData = new HashMap<>();
				resData.put("count", jsonResponse.getLong("count"));
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.data(resData)
					.error(ErrorCode.DUPLICATE_EMAIL)
					.build();
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INTERNAL_SERVER_ERROR).build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// nickname 존재여부
	@Override
	public ResponseEntity<ResponseDto> checkNickname(String id, String nickname) {
		try {
			ResponseEntity<String> result = restTemplate.getForEntity(
				domainUrl + "members/nickname/{id}/{nickname}", String.class, id, nickname);

			JSONObject jsonResponse = new JSONObject(result.getBody());
			log.warn(jsonResponse);

			if (jsonResponse.getString("count").equals("0") || jsonResponse.getString("currentNickname")
				.equals(nickname)) {

				Map<String, String> resData = new HashMap<>();
				resData.put("count", jsonResponse.getString("count"));
				resData.put("currentNickname", jsonResponse.getString("currentNickname"));

				ResponseDto response = ResponseDto.builder().success(true).data(resData).build();

				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				Map<String, String> resData = new HashMap<>();
				resData.put("count", jsonResponse.getString("count"));
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.data(resData)
					.error(ErrorCode.DUPLICATE_NICKNAME)
					.build();

				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.INTERNAL_SERVER_ERROR).build();
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 비밀번호 수정 - 현재 사용자의 비밀번호가 입력한 현재 비밀번호와 일치하는지
	@Override
	public String checkPassword(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity(domainUrl + "members/password/{id}",
			String.class, id);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		JSONObject jsonPassword = new JSONObject(response.getBody());
		String password = jsonPassword.getString("password");

		return password;
	}
}