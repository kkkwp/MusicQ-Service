package com.musicq.musicqservice.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.musicq.musicqservice.member.util.TokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {

	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		// Authorization 헤더에서 access token을 추출합니다.
		String authorizationHeader = request.getHeader("Cookie");
		log.warn(authorizationHeader);

		if (authorizationHeader == null || !authorizationHeader.startsWith("Authorization=")) {
			// access token이 없거나, Authorization 으로 시작하지 않으면 허용 X
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		String accessToken = authorizationHeader.substring(14);
		String userId = tokenProvider.getId(accessToken);

		String tokenFromRedis = redisTemplate.opsForValue().get(userId);
		String existToken = tokenFromRedis != null ? tokenFromRedis.substring(1) : null;

		// access token이 redis 에 존재하는지 유무로 유효한지 확인합니다.
		if (!accessToken.equals(existToken) || existToken.equals(null)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		// access token이 유효하면 요청을 허용합니다.
		return true;
	}
}
