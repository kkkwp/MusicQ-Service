package com.musicq.musicqservice.member.service;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.member.dto.LoginDto;
import com.musicq.musicqservice.member.dto.LoginResDto;
import com.musicq.musicqservice.member.util.Encoder;
import com.musicq.musicqservice.member.util.TokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService{
	@Value("${jwt.header}")
	private String jwtHeader;

	@Value("${Cookie-Max-Age}")
	private int maxAge;
	private final RestTemplate restTemplate;
	private final TokenProvider tokenProvider;

	private final RedisTemplate<String, String> redisTemplate;

	// 로그인
	@Override
	public ResponseEntity<LoginResDto> login(LoginDto loginDto, HttpServletRequest request) {
		// Cookie 에 Access Token 존재 여부
		String accessToken = chkCookieInToken(request);

		// Cookie를 set 하기 위한 Http header
		HttpHeaders httpHeaders = new HttpHeaders();

		HttpStatus status = HttpStatus.UNAUTHORIZED;

		// body에 출력할 login에 대한 결과
		LoginResDto loginResDto = new LoginResDto();


		// Cookie에 Access Token이 없는 경우
		if(accessToken == null){
			// id 존재 여부
			JSONObject jsonIdCount = new JSONObject(checkId(loginDto.getId()).getBody());
			long idCount = jsonIdCount.getLong("count");
			log.warn(idCount);

			// DB에 Client에서 일치한 사용자 정보가 있을 때
			if(idCount == 1){
				// DB에 저장된 Member 비밀 번호와 현재 입력한 Member의 비밀번호를 비교
				String password = checkPassword(loginDto.getId());

				if (Encoder.isMatch(loginDto.getPassword(), password)) {
					ResponseEntity<String> loginDomainRes = restTemplate.postForEntity("http://localhost:81/v1/members/login", loginDto, String.class);
					log.info(loginDomainRes.getStatusCode());
					log.info(loginDomainRes.getHeaders());
					log.info(loginDomainRes.getBody());

					// Domain Application 으로 부터 가져온 Member정보로 토큰 발근
					accessToken = tokenProvider.createAccessToken(loginDomainRes);

					// Redis에 ID - JWT Token 쌍으로 삽입, 이 때 Redis의 유효기간을 설정하므로 JWT에는 유효기간 설정 X
					// Cookie에 token이 없고 Redis에 존재하는 예외는 Redis에 삽입할 때 key와 같은 key로 삽입하면 자동 업데이트된다.
					boolean redisSaveToken = saveToken(loginDto.getId(), accessToken);

					if(redisSaveToken){
						// Cookie에 Authorizaion 이름으로 AccessToken 삽입
						httpHeaders.add(HttpHeaders.SET_COOKIE, setCookieToken(accessToken));

						// 결과
						status = HttpStatus.OK;
						loginResDto.setResult("Success");
					} else {
						status = HttpStatus.BAD_REQUEST;
						loginResDto.setResult("교통사고");
					}
				}
				else {
					// 입력된 ID가 존재하나 그 ID에 해당되는 PW와 입력한 PW가 일치하지 않는 경우
					loginResDto.setResult("Wrong PW");
				}
			} else if (idCount == 0){
				// id가 존재하지 않는 경우
				loginResDto.setResult("Wrong ID");
			}
			else {
				log.warn("뭔가 잘못됨");
			}
		// Cookie에 Access Token이 존재하는 경우
		} /*else{
		}*/
		log.warn(loginResDto);
		return new ResponseEntity<>(loginResDto, httpHeaders, status);
	}


	// id 존재 여부
	@Override
	public ResponseEntity<String> checkId(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/id/{id}", String.class, id);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());
		return response;
	}

	// 로그인 시 입력한 id를 가지고 DB에 저장된 Member 비밀 번호와 현재 입력한 Member의 비밀번호를 비교하기 위한 메서드
	@Override
	public String checkPassword(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/password/{id}", String.class, id);
		log.info(response.getStatusCode());
		log.info(response.getHeaders());
		log.info(response.getBody());

		JSONObject jsonPassword = new JSONObject(response.getBody());
		String password = jsonPassword.getString("password");

		return password;
	}

	@Override
	// httpHeader 에 Set-Cookie 로 add해서 응답하기 위해서 문자열 포맷으로 헤더 작성하는 메서드
	public String setCookieToken(String accessToken){
		String cookieSet = String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d", jwtHeader, accessToken, maxAge);
		return cookieSet;
	}

	@Override
	// 클라이언트의 login 요청 시 Cookie 에 MusicQ 에서 발급한 Access token이 존재한다면 Token을 아니면 null을 반환하는 메서드
	public String chkCookieInToken(HttpServletRequest request){
		// 클라이언트 쿠키 정보 가져오기
		Cookie[] cookies = request.getCookies();

		// Cookie 값 중 해당 서비스에서 발급한 헤더의 이름에 맞는 Cookie값이 있는지 확인
		String accessToken = null;

		if(cookies != null) {
			for (Cookie cookie : cookies){
				if(cookie.getName().equals(jwtHeader)){
					accessToken = cookie.getValue();
					break;
				}
			}
		}
		return accessToken;
	}

	// 로그인 성공 시 Redis에 사용자의 ID를 KEY로 생성한 Access Token을 Value로 해서 Redis에 삽입하는 메서드로 성공 여부에 따른 Bool값 반환
	@Override
	public boolean saveToken(String id, String accessToken) {
		boolean result = false;
		try {
			redisTemplate.opsForValue().set(id, accessToken, TimeUnit.DAYS.toDays(1));
			result = true;
		} catch (Exception e){
			log.warn(e);
		}
		return result;
	}

	@Override
	public String getToken(String id) {
		String accessToken = redisTemplate.opsForValue().get(id);
		return accessToken;
	}

}
