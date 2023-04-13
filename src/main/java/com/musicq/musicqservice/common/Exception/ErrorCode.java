package com.musicq.musicqservice.common.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
	INVALID_INPUT_VALUE(400, "COMMON-001", "유효성 검증에 실패한 경우(Client 의 잘못된 Data 요청)."),
	INTERNAL_SERVER_ERROR(500, "COMMON-002", "서버에서 처리할 수 없는 경우."),

	NOT_EXIST_ID(400, "Member-001", "ID가 존재하지 않는 경우."),
	NOT_EXIST_PW(400, "Member-002", "ID는 존재하나 ID에 맞는 PW가 아닌 경우."),
	EXPIRED_TOKEN(401, "Member-003", "발급받은 토큰이 만료되어 redis에 존재하지 않는 경우."),
	NOT_EXIST_TOKEN_IN_COOKIE(405, "Member-004", "Cookie에 Token이 없는데 자동로그인 요청을 한 경우."),
	EXIST_TOKEN_IN_COOKIE(405, "Member-005", "Cookie에 Token이 있는데 로그인 요청을 한 경우."),
	DUPLICATE_ID(400, "Member-006", "이미 존재하는 ID가 입력된 경우."),
	DUPLICATE_NICKNAME(400, "Member-007", "이미 존재하는 NickName이 입력된 경우."),
	DUPLICATE_EMAIL(400, "Member-008", "이미 존재하는 Email이 입력된 경우."),
	INVALID_INPUT_ID(400, "Member-009", "아이디는 알파벳과 숫자가 포함되지 않거나 6~20자리의 입력이 아니거나 공백이 포함된 경우."),
	INVALID_INPUT_PW(400, "Member-0010", "비밀번호에 영문, 숫자, 특수문자가 모두 포함되지 않거나 공백이나 다른 문자가 입력됬거나 8~16자리 입력이 아닌 경우"),
	INVALID_INPUT_NICKNAME(400, "Member-011", "닉네임이 특수문자나 공백을 포함하거나 2~10자리 입력이 아닌 경우."),
	INVALID_INPUT_EMAIL(400, "Member-012", "이메일 형식이 올바르지 않은 경우."),
	NOT_EQUALS_INPUT_CHANGED_PW(400, "Member-013", "변경할 비밀번호 입력과 확인하는 입력이 일치하지 않는 경우"),
	NOT_EQUALS_INPUT_CURRENT_PW(400, "Member-014", "현재 입력한 비밀번호가 실제 현재 비밀번호와 일치하지 않는 경우");

	private int status;
	private String code;
	private String description;
}