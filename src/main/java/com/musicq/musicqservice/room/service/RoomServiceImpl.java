package com.musicq.musicqservice.room.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.room.dto.RoomCreateDto;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Service
public class RoomServiceImpl implements RoomService {
	private final RestTemplate restTemplate;

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

	// 방 생성 - session 생성
	@Override
	public ResponseEntity<String> createSession(Map<String, Object> params, HttpServletRequest request)
		throws OpenViduJavaClientException, OpenViduHttpException {
		SessionProperties properties = SessionProperties.fromJson(params).build();
		Session session = openVidu.createSession(properties);
		return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> createRoom(String sessionId, RoomCreateDto roomCreateDto) {
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:81/v1/rooms/create/{sessionId}",
			roomCreateDto, String.class, sessionId);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}

	// 방 입장 - connection 생성
	// TODO: Redis update
	public ResponseEntity<String> createConnection(String sessionId, Map<String, Object> params)
		throws OpenViduJavaClientException, OpenViduHttpException {
		Session session = openVidu.getActiveSession(sessionId);
		if (session == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
		Connection connection = session.createConnection(properties);
		return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
	}

	// 방 정보 조회
	@Override
	public ResponseEntity<String> enter(String roomId) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/rooms/enter/{roomId}",
			String.class, roomId);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	@Override
	public ResponseEntity<String> deleteRoom(String roomId) {
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:81/v1/rooms/delete/{roomId}",
			HttpMethod.DELETE, HttpEntity.EMPTY, String.class, roomId);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}

	@Override
	public ResponseEntity<Object> searchAll(Integer page) {
		String baseUrl = "http://localhost:81/v1/rooms/all";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(baseUrl).append("?page=").append(page);
		String searchingUrl = stringBuilder.toString();
		ResponseEntity<Object> response = restTemplate.getForEntity(searchingUrl,
			Object.class);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}
}

