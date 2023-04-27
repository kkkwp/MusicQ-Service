package com.musicq.musicqservice.member.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.musicq.musicqservice.common.Exception.ErrorCode;
import com.musicq.musicqservice.common.ResponseDto;
import com.musicq.musicqservice.member.dto.ResultResDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;

	@Value("${setFromEmail}")
	private String setFromEmail;

	// 리퍼럴 추적 방식의 메서드인데 일단 리퍼럴 자체가 한 도메인 내의 이전 페이지에 대한 기록을 보여주는 거라 실패
	@Override
	public ResponseEntity<ResultResDto> sendMail1(String email) {

		try {
			String setFrom = setFromEmail;
			String htmlContent = "<h1>손병주 바보</h1><a href='http://localhost/api/v1/members/email/authentication/referer'>http://localhost/api/v1/members/email/authentication/referer</a>";

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper httpMail = new MimeMessageHelper(message, true, "UTF-8");
			httpMail.setFrom(setFrom);
			httpMail.setTo(email);
			httpMail.setSubject("손병주 바보");
			httpMail.setText(htmlContent, true);

			javaMailSender.send(message);
			ResultResDto response = new ResultResDto("Success");
			return ResponseEntity.ok(response);
		} catch (NullPointerException | IllegalArgumentException | HttpMessageNotWritableException |
				 MessagingException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());
			ResultResDto response = new ResultResDto("Failed");
			return ResponseEntity.badRequest().body(response);
		}
	}

	// 인증 코드를 API 에 던져주고 이를 Client 에 던져주는 동시에 이메일과 코드를 입력하는 링크(Client)를 같이 주면 이를 비교해서 인증하는 방식
	// React 단에서 setTime을 지정해서 만료를 설정할 예정 약 3분이 적당 할듯.
	@Override
	public ResponseEntity<ResponseDto> sendMail(String email) {
		try {
			String setFrom = setFromEmail;
			String htmlContent1 = "<h2>MuisicQ 이메일 인증</h2>";
			String key = createAuthKey();
			String htmlContent2 = "<h3>인증 번호는 " + key + " 입니다, 회원가입 페이지로 돌아가서 6자리 코드를 입력해 주세요 제한시간은 3분입니다.</h3>";

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper httpMail = new MimeMessageHelper(message, true, "UTF-8");
			httpMail.setFrom(setFrom);
			httpMail.setTo(email);
			httpMail.setSubject("MusicQ 회원 가입 인증 확인");
			httpMail.setText(htmlContent1 + htmlContent2, true);

			javaMailSender.send(message);
			log.warn(key);
			HttpStatus status = HttpStatus.OK;
			ResponseDto response = ResponseDto.builder()
				.success(true)
				.data(key)
				.build();
			return new ResponseEntity<>(response, status);
		} catch (NullPointerException | IllegalArgumentException | HttpMessageNotWritableException |
				 MessagingException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());

			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			ResponseDto response = ResponseDto.builder()
				.success(false)
				.error(ErrorCode.INTERNAL_SERVER_ERROR)
				.build();
			return new ResponseEntity<>(response, status);
		}
	}

	// 리퍼럴 확인 메서드 -> 찾아보니 리퍼럴은 한 도메인 내의 이전페이지에서 접속했을 때만 기록을 추적할 수 있다...
	@Override
	public ResponseEntity<ResultResDto> checkReferer(HttpServletRequest request) {
		try {
			String referer = request.getHeader("Referer");
			log.warn(request.getCookies());
			ResultResDto response = new ResultResDto(referer);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException | NullPointerException e) {
			log.warn(e.getStackTrace());
			log.warn(e.getMessage());
			ResultResDto response = new ResultResDto("Failed");
			return ResponseEntity.badRequest().body(response);
		}
	}

	// 6자리 인증 코드 생성 함수
	@Override
	public String createAuthKey() {
		StringBuffer key = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 6; i++) {
			int index = rnd.nextInt(2);
			if (index == 0) { // 알파벳
				key.append(((char)(rnd.nextInt(26) + 97)));
			} else { // 숫자
				key.append((rnd.nextInt(10)));
			}
		}
		return key.toString();
	}
}