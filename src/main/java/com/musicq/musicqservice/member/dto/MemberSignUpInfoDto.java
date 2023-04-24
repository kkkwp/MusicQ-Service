package com.musicq.musicqservice.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpInfoDto {
	@NotNull
	@NotBlank(message = "아이디는 필수 입력 값입니다.")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]{6,20}$", message = "아이디는 알파벳과 숫자를 반드시 포함한 6~20자리여야 합니다.")
	private String id;

	@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Setter
	private String email;

	@NotNull
	@NotBlank(message = "닉네임은 필수 입력 값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
	@Setter
	private String nickname;

	@NotNull
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
	@Setter
	private String password;

	@Builder.Default
	@Setter
	private MemberImageDto memberImage = MemberImageDto.builder().build();

}
