package com.musicq.musicqservice.game.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musicq.musicqservice.game.service.GameService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/games")
public class GameController {
	private final GameService gameService;

	// 가사 출력
	// 음성 출력은 React단 에서
	@GetMapping("/lyrics")
	public ResponseEntity<Object> playLyrics(
		@RequestParam(name = "id") String[] musicIdList
	) {
		//return ResponseEntity.ok(musicIdList);
		return gameService.playLyrics(musicIdList);
	}
}
