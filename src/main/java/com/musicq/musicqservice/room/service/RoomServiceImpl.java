package com.musicq.musicqservice.room.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.common.Exception.ErrorCode;
import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.room.dto.RoomCreateDto;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Service
public class RoomServiceImpl implements RoomService {
	// host를 제외하고, 방의 최대 인원
	private static final int LIMIT = 5;

	private final RestTemplate restTemplate;

	@Value("${domainApplication.url}")
	private String domainUrl;

	// 오픈비두 서버 관련 변수
	@Value("${openvidu.url}")
	private String OPENVIDU_URL;
	@Value("${openvidu.secret}")
	private String OPENVIDU_SECRET;
	private OpenVidu openVidu;

	@PostConstruct
	public void initSession() {
		this.openVidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
	}

	@Override
	public ResponseEntity<String> createSession(Map<String, Object> params, HttpServletRequest request)
		throws OpenViduJavaClientException, OpenViduHttpException {
		SessionProperties properties = SessionProperties.fromJson(params).build();
		Session session = openVidu.createSession(properties);
		return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseDto> createRoom(String sessionId, RoomCreateDto roomCreateDto) {
		try {
			ResponseEntity<Object> result = restTemplate.postForEntity(
				domainUrl + "rooms/create/{sessionId}",
				roomCreateDto, Object.class, sessionId);
			ResponseDto<Object> response = ResponseDto.builder()
				.success(true)
				.data(result.getBody())
				.build();
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			ResponseDto<Object> response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.DUPLICATE_ROOM)
				.build();
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<ResponseDto> enterRoom(String sessionId, Map<String, Object> params)
		throws OpenViduJavaClientException, OpenViduHttpException {
		Session session = openVidu.getActiveSession(sessionId);
		if (session == null) {
			ResponseDto<Object> response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.NOT_EXIST_ROOM)
				.build();
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		List<Connection> activeConnections = session.getActiveConnections();
		if (activeConnections.size() >= LIMIT) {
			ResponseDto<Object> response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.OVERCAPACITY)
				.build();
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
		Connection connection = session.createConnection(properties);
		ResponseDto<Object> response = ResponseDto.builder()
			.success(true)
			.data(connection.getToken())
			.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteRoom(String roomId, HttpServletResponse cookieRes) {
		destroyCookieToken(cookieRes);
		ResponseEntity<String> response = restTemplate.exchange(domainUrl + "rooms/delete/{roomId}",
			HttpMethod.DELETE, HttpEntity.EMPTY, String.class, roomId);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	@Override
	public ResponseEntity<Object> searchAll(Integer page, HttpServletResponse cookieRes) {
		destroyCookieToken(cookieRes);
		String baseUrl = domainUrl + "rooms/all";
		String searchingUrl = baseUrl + "?page=" + page;
		ResponseEntity<Object> response = restTemplate.getForEntity(searchingUrl,
			Object.class);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	@Override
	public void destroyCookieToken(HttpServletResponse cookieRes) {
		Cookie cookie = new Cookie("OVJSESSIONID", null);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookieRes.addCookie(cookie);
	}
}

