package com.musicq.musicqservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 프로그램에서 제공하는 URL
			.allowedOrigins("*") // 요청을 허용할 출처를 명시, 전체 허용
			.allowedHeaders("*") // 어떤 헤더들을 허용할 것인지
			.allowedMethods("*"); // 어떤 메서드를 허용할 것인지
	}
}
