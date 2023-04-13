package com.musicq.musicqservice.game.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface GameService {
	public ResponseEntity<Object> playLyrics(String[] musicIdList);
}
