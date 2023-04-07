package com.musicq.musicqservice.music.service;

import org.springframework.http.ResponseEntity;

public interface MusicService {

	public ResponseEntity<String> searchAllMusics();
}
