package com.musicq.musicqservice.room.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.musicq.musicqservice.room.dto.RoomCreateDto;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import jakarta.servlet.http.HttpServletRequest;

public interface RoomService {
	ResponseEntity<String> createSession(Map<String, Object> params, HttpServletRequest request) throws
		OpenViduJavaClientException,
		OpenViduHttpException;

	ResponseEntity<String> createRoom(String sessionId, RoomCreateDto roomCreateDto);

	ResponseEntity<String> createConnection(String sessionId, Map<String, Object> params) throws
		OpenViduJavaClientException,
		OpenViduHttpException;

	ResponseEntity<String> enter(String roomId);

	ResponseEntity<String> deleteRoom(String roomId);

	ResponseEntity<Object> searchAll(Integer page);
}
