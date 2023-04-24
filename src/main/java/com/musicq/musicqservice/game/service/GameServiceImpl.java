package com.musicq.musicqservice.game.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {
	private final RestTemplate restTemplate;

	@Value("${domainApplication.url}")
	private String domainUrl;

	@Override
	public ResponseEntity<Object> playLyrics(String[] musicIdList) {
		// url에 쿼리 파라미터 전달하기 위한 builder
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(domainUrl + "lyrics/")
			.queryParam("id", musicIdList);

		ResponseEntity<Object> response = restTemplate.getForEntity(builder.build().toUri(), Object.class);

		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}
}
