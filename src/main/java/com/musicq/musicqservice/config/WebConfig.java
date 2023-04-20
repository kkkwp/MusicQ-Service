package com.musicq.musicqservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final JwtTokenInterceptor jwtTokenInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 프로그램에서 제공하는 URL
			/*allowedOrigins("*")를 사용하여 모든 출처(Origin)를 허용하고 있기 때문에,
			서버 측에서 Access-Control-Allow-Origin 응답 헤더의 값이 와일드카드(*)로 설정됩니다. 이 경우, 요청의 credentials 옵션이
			"include"로 설정되어 있으면, CORS 정책에 의해 차단되기 때문에 정확한 도메인을 설정해야 한다.*/
			.allowedOrigins("http://localhost:3000") // 요청을 허용할 출처를 명시, 전체 허용
			.allowedHeaders("*") // 어떤 헤더들을 허용할 것인지
			.allowedMethods("*") // 어떤 메서드를 허용할 것인지
			// credentials 옵션을 "include"로 설정하면, 서버로 요청 시 쿠키를 함께 보내도록 설정
			.allowCredentials(true)
			// 서버로 부터의 요청을 허용하는 시간 (일정 시간 지나면 제거) -> 서버 부하 감소
			.maxAge(3600);
	}

	// Cookie 에서 AccessToken 을 꺼내서 확인하기 위한 설정으로 이 검증을 적용할 API 와 제외할 API 설정
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtTokenInterceptor)
			.addPathPatterns("/api/v1/**")
			.excludePathPatterns("/api/v1/members/login",
				"/api/v1/members/member",
				"/api/v1/members/token",
				"/api/v1/members/id/{id}",
				"/api/v1/members/email/{email}",
				"/api/v1/members/nickname/{id}/{nickname}",
				"/api/v1/members/email/authentication/{email}");
	}
}
