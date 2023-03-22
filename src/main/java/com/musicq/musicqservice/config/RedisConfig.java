package com.musicq.musicqservice.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

// Service 계층에서 사용할 수 있게
@Configuration

// Redis 레포지토리를 사용할 수 있도록 지원하는 어노테이션
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
	private final RedisProperties redisProperties;

	// Redis 연결을 위한 LettuceConnectionFactory 를 Bean으로 생성
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	// Redis 데이터를 저장하고 검색하는데 사용하는 RedisTemplate 를 생성한다.
	// 해당 Bean은 RedisConnectionFactory를 의존성으로 갖으며 StringRedisSerializer를 사용해서 key와 value를 직려화 한다.
	// 이렇게 되면 Redis에 대한 CRUD 작업을 수행할 수 있다.
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}
}
