package com.musicq.musicqservice.music.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicq.musicqservice.music.service.MusicService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/musics")
public class MusicController {

	private final MusicService musicService;

	@GetMapping("/searchAllMusics")
	public ResponseEntity<String> searchAll(){

		return musicService.searchAllMusics();
	}
}
