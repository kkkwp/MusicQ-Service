package com.musicq.musicqservice.member.util;

import java.security.Key;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider implements InitializingBean {
	private Key key;

	@Value("${jwt.secret}")
	private String secret;

	// InitializingBean 을 implements 해서 afterPropertiesSet 를 재정의하는 이유는
	// Bean 이 생성되고 secret 값과 만료시간을 의존성 주입 받은 뒤에 Secret 값을 Base64 Decode 한 후 key 변수에 할당하기 위함이다.
	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	// Access Token 발급 메서드
	public String createAccessToken(ResponseEntity<String> response) {
		// Redis 사용으로 인한 유효기간 의미 X
		//long now = (new Date()).getTime();
		//Date accessValidity = new Date(now + this.accessTokenValidityInMilliseconds * 1000);

		JSONObject responseBody = new JSONObject(response.getBody());
		String id = responseBody.getString("id");
		String email = responseBody.getString("email");
		String nickname = responseBody.getString("nickname");

		String accessToken = Jwts.builder()
			.setSubject(id)
			.claim("id", id)
			.claim("email", email)
			.claim("nickname", nickname)
			.setIssuer("MusicQ")
			//.setExpiration(accessValidity)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		return accessToken;
	}

	// Access Token에서 id 조회 메서드
	public String getId(String accessToken) {
		Claims claims = parseClaims(accessToken);

		if (claims.get("id") != null) {
			return claims.get("id").toString();
		} else {
			return null;
		}
	}

	// 토큰 복호화 메서드
	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}
