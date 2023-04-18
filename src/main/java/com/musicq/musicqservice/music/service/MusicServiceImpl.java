package com.musicq.musicqservice.music.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MusicServiceImpl implements MusicService {

	private final RestTemplate restTemplate;

	@Override
	public ResponseEntity<String> searchAllMusics() {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/musics/all",
			String.class);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		return response;
	}
}
