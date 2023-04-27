package com.musicq.musicqservice.room.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.room.dto.RoomCreateDto;
import com.musicq.musicqservice.room.service.RoomService;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {

	private final RoomService roomService;

	// 방 생성(POST)
	@PostMapping("/create")
	public ResponseEntity<String> createSession(
		@Valid @RequestBody(required = false) Map<String, Object> params,
		@Valid HttpServletRequest request
	) throws OpenViduHttpException, OpenViduJavaClientException {
		return roomService.createSession(params, request);
	}

	@PostMapping("/create/{sessionId}")
	public ResponseEntity<ResponseDto> createRoom(
		@Valid @PathVariable String sessionId,
		@Valid @RequestBody RoomCreateDto roomCreateDto
	) {
		return roomService.createRoom(sessionId, roomCreateDto);
	}

	// 방 입장(POST)
	@PostMapping("/enter/{sessionId}")
	public ResponseEntity<ResponseDto> enterRoom(
		@Valid @PathVariable("sessionId") String sessionId,
		@Valid @RequestBody(required = false) Map<String, Object> params
	) throws OpenViduJavaClientException, OpenViduHttpException {
		return roomService.enterRoom(sessionId, params);
	}

	// 방 삭제(DELETE)
	@DeleteMapping("/delete/{roomId}")
	public ResponseEntity<String> deleteRoom(
		@Valid @PathVariable("roomId") String roomId,
		@Valid HttpServletResponse cookieRes
	) {
		return roomService.deleteRoom(roomId, cookieRes);
	}

	/*// 방 전체 조회(Paging 처리)
	@GetMapping("/all")
	public ResponseEntity<Object> searchAll(
		@Valid @RequestParam(value = "page", required = false) Integer page
	) {
		log.info("page엔 뭐가 있을까~~~요? {}", page);
		if (page == null) {
			page = 1;
		}
		return roomService.searchAll(page);
	}*/

	// 10초 주기로 Client 측으로 실시간 조회를 해줌.
	// 근데 Client 측에서 어떤 이벤트 네임을 걸어주면 내가 필요할 때만 응답을 줄 수 있다고 알고 있음.
	// 추 후에 React를 만든 후에 어떻게 해야될지 고민해봐야될 것 같음.
	@GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ResponseEntity<Object>> searchAll(
		@Valid @RequestParam(value = "page", required = false) Integer page,
		@Valid HttpServletRequest request,
		@Valid HttpServletResponse cookieRes
	) {
		if (page == null) {
			page = 1;
		}

		Integer rambdaPage = page;
		// 10초 주기로 응답을 해주기 위한 Flux 객체
		Flux<ResponseEntity<Object>> delayed = Mono.fromCallable(() -> {
				return roomService.searchAll(rambdaPage, request, cookieRes);
			})
			.repeat()
			.delayElements(Duration.ofSeconds(1));

		return delayed;
	}
}