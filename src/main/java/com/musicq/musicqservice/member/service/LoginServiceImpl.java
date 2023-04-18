package com.musicq.musicqservice.member.service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.musicq.musicqservice.common.Exception.ErrorCode;
import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.LoginDto;
import com.musicq.musicqservice.member.util.Encoder;
import com.musicq.musicqservice.member.util.TokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
	@Value("${jwt.header}")
	private String jwtHeader;

	@Value("${Cookie-Max-Age}")
	private int maxAge;
	private final RestTemplate restTemplate;
	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	// 로컬 로그인
	@Override
	public ResponseEntity<ResponseDto> login(LoginDto loginDto, HttpServletRequest request) {
		// Cookie 에 Access Token 존재 여부
		String accessToken = chkTokenInCookie(request);

		// Cookie를 set 하기 위한 Http header
		HttpHeaders httpHeaders = new HttpHeaders();

		// Cookie에 Access Token이 없는 경우
		if (accessToken == null) {
			// id 존재 여부
			JSONObject jsonIdCount = new JSONObject(checkId(loginDto.getId()).getBody());
			long idCount = jsonIdCount.getLong("count");

			// DB에 Client에서 일치한 사용자 정보가 있을 때
			if (idCount == 1) {
				// DB에 저장된 Member 비밀 번호와 현재 입력한 Member의 비밀번호를 비교
				String password = checkPassword(loginDto.getId());

				if (Encoder.isMatch(loginDto.getPassword(), password)) {
					ResponseEntity<String> loginDomainRes = restTemplate.postForEntity(
						"http://localhost:81/v1/members/login", loginDto, String.class);
					log.info(loginDomainRes.getStatusCode());
					log.info(loginDomainRes.getHeaders());
					log.info(loginDomainRes.getBody());

					// Domain Application 으로 부터 가져온 Member 정보로 토큰 발급
					accessToken = tokenProvider.createAccessToken(loginDomainRes);

					// Redis에 (ID - JWT Token) 쌍으로 삽입, 이 때 Redis에 유효기간을 설정하므로 JWT에는 유효기간 설정 X
					// Cookie에 token이 없고 Redis에 존재하는 예외는 Redis에 삽입할 때 key와 같은 key로 삽입하면 자동 업데이트된다.
					boolean redisSaveToken = saveToken(loginDto.getId(), accessToken);

					if (redisSaveToken) {
						// Cookie에 Authorizaion 이름으로 AccessToken 삽입
						httpHeaders.add(HttpHeaders.SET_COOKIE, setCookieToken(accessToken));

						// 결과
						HttpStatus status = HttpStatus.OK;
						ResponseDto response = ResponseDto.builder().success(true).build();
						// 로그인 성공 결과와 헤더에 Cookie 생성 후 AccessToken 발급
						return new ResponseEntity<>(response, httpHeaders, status);
					} else {
						HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
						ResponseDto response = ResponseDto.builder()
							.success(false)
							.error(ErrorCode.INTERNAL_SERVER_ERROR)
							.build();
						return new ResponseEntity<>(response, status);
					}
				} else {
					// 입력된 ID가 존재하나 그 ID에 해당되는 PW와 입력한 PW가 일치하지 않는 경우
					HttpStatus status = HttpStatus.BAD_REQUEST;
					ResponseDto response = ResponseDto.builder()
						.success(false)
						.error(ErrorCode.NOT_EXIST_PW)
						.build();
					return new ResponseEntity<>(response, status);
				}
			} else if (idCount == 0) {
				// id가 존재하지 않는 경우
				HttpStatus status = HttpStatus.BAD_REQUEST;
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.NOT_EXIST_ID)
					.build();
				return new ResponseEntity<>(response, status);
			} else {
				log.warn("회원 중복되어 있어 이미 이멀전씌");
				HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.INTERNAL_SERVER_ERROR)
					.build();
				return new ResponseEntity<>(response, status);
			}
		} else {
			log.warn("Cookie에 AccessToken이 있으면 login 요청에 바로오면 안돼용");
			HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
			ResponseDto response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.EXIST_TOKEN_IN_COOKIE)
				.build();
			return new ResponseEntity<>(response, status);
		}
	}

	// 자동 로그인
	// 로그인 유효기간(1일)이 지나지 않았다면 자동로그인을 해주고, 1일을 연장해준다.
	@Override
	public ResponseEntity<ResponseDto> autoLogin(HttpServletRequest request) {
		// Cookie에 Access Token 존재 여부
		String tokenInCookie = chkTokenInCookie(request);

		// Cookie에 Access Token이 있다면
		if (tokenInCookie != null) {
			// Access Token으로부터 id 추출
			String id = tokenProvider.getId(tokenInCookie);
			log.warn(tokenInCookie);
			if (id != null) {
				// Redis에서 id에 해당하는 토큰을 얻어옴
				String tokenInRedis = getToken(id);

				// Redis에 (해당 id - 해당 토큰)이 존재 한다면
				if (tokenInRedis.equals(tokenInCookie)) {
					// 유효기간 1일 늘려서 Redis에 다시 저장
					boolean redisSaveToken = saveToken(id, tokenInRedis);

					if (redisSaveToken) {
						// 결과
						HttpStatus status = HttpStatus.OK;
						ResponseDto response = ResponseDto.builder().success(true).build();
						return new ResponseEntity<>(response, status);
					} else {
						HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
						ResponseDto response = ResponseDto.builder()
							.success(false)
							.error(ErrorCode.INTERNAL_SERVER_ERROR)
							.build();
						return new ResponseEntity<>(response, status);
					}
				} else {
					// Redis에 존재하지 않는다면 로그인 유효기간이 지났단 뜻이므로 일반 로그인으로 리다이렉트
					// 이 응답 코드는 요청한 리소스의 URI가 일시적으로 변경되었음을 의미
					HttpStatus status = HttpStatus.FOUND;
					ResponseDto response = ResponseDto.builder().success(false).error(ErrorCode.EXPIRED_TOKEN).build();
					return new ResponseEntity<>(response, status);
				}
			} else {
				log.warn("토큰에 해당하는 id가 없다고?");
				HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
				ResponseDto response = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.INTERNAL_SERVER_ERROR)
					.build();
				return new ResponseEntity<>(response, status);
			}
		} else {
			log.warn("Cookie에 Access Token이 존재해야 자동 로그인 가능해용");
			HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
			ResponseDto response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.NOT_EXIST_TOKEN_IN_COOKIE)
				.build();
			return new ResponseEntity<>(response, status);
		}
	}

	//로그 아웃
	@Override
	public ResponseEntity<ResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Cookie에 Access Token 존재 여부
			String tokenInCookie = chkTokenInCookie(request);
			String userId = tokenProvider.getId(tokenInCookie);
			String existRedisInToken = getToken(userId);

			if (tokenInCookie.equals(existRedisInToken)) {
				redisTemplate.delete(userId);

				// 클라이언트에게 파괴할 Cookie를 보냄
				Cookie cookie = new Cookie("Authorization", "");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);

				ResponseDto result = ResponseDto.builder()
					.success(true)
					.build();
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				ResponseDto result = ResponseDto.builder()
					.success(false)
					.error(ErrorCode.INTERNAL_SERVER_ERROR)
					.build();
				log.warn("로그아웃을 한다는건 로그인이 이미 되어 있어야 하는데 redis에 없다 진짜 사고임");
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			ResponseDto result = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.INTERNAL_SERVER_ERROR)
				.build();
			log.warn(e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// id 존재 여부
	@Override
	public ResponseEntity<String> checkId(String id) {
		ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:81/v1/members/id/{id}",
			String.class, id);
		return result;
	}

	// 로그인 시 입력한 id를 가지고 DB에 저장된 Member 비밀 번호와 현재 입력한 Member의 비밀번호를 비교하기 위한 메서드
	@Override
	public String checkPassword(String id) {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:81/v1/members/password/{id}",
			String.class, id);
		JSONObject jsonPassword = new JSONObject(response.getBody());
		String password = jsonPassword.getString("password");

		return password;
	}

	// httpHeader 에 Set-Cookie 로 add해서 응답하기 위해서 문자열 포맷으로 헤더 작성하는 메서드
	@Override
	public String setCookieToken(String accessToken) {
		String cookieSet = String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d", jwtHeader, accessToken, maxAge);
		return cookieSet;
	}

	// 클라이언트의 login 요청 시 Cookie 에 MusicQ 에서 발급한 Access token이 존재한다면 Token을, 아니면 null을 반환하는 메서드
	@Override
	public String chkTokenInCookie(HttpServletRequest request) {
		// 클라이언트 쿠키 정보 가져오기
		Cookie[] cookies = request.getCookies();

		// Cookie 값 중 해당 서비스에서 발급한 헤더의 이름에 맞는 Cookie값이 있는지 확인
		String accessToken = null;

		try {
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(jwtHeader)) {
						accessToken = cookie.getValue();
						break;
					}
				}
			}
		} catch (NullPointerException e) {
			// 예외 처리
			accessToken = null;
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
		} catch (Exception e) {
			log.warn(e);
		}
		return result;
	}

	// Redis에 (id - access token) 쌍이 존재한다면 그 access token을 반환하는 메서드
	@Override
	public String getToken(String id) {
		try {
			String accessToken = Objects.requireNonNull(redisTemplate.opsForValue().get(id)).trim();
			return accessToken;
		} catch (NullPointerException e) {
			// 예외 처리
			return null;
		}
	}
}