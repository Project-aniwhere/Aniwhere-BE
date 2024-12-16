package com.example.aniwhere.global.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀 예외 코드
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode {

	// common
	INTERNAL_SERVER_ERROR(500, "C001", "internal server error"),
	INVALID_INPUT_VALUE(400, "C002", "invalid input type"),
	METHOD_NOT_ALLOWED(405, "C003", "method not allowed"),
	INVALID_TYPE_VALUE(400, "C004", "invalid type value"),
	BAD_CREDENTIALS(400, "C005", "bad credentials"),

	// member
	INVALID_TOKEN(401, "M001", "유효하지 않은 토큰입니다."),
	INVALID_CREDENTIAL(401, "M002", "유효하지 않은 인증 정보입니다."),
	DUPLICATED_NICKNAME(400, "M003", "중복된 닉네임입니다."),
	DUPLICATED_EMAIL(400, "M004", "중복된 메일입니다."),
	EMAIL_VERIFICATION_FAIL(400, "M005", "인증에 실패했습니다."),
	VERIFICATION_CODE_MISMATCH(400, "M006", "인증 코드가 일치하지 않습니다."),
	PASSWORD_MISMATCH(400, "M007", "비밀번호가 일치하지 않습니다."),
	NOT_FOUND_USER(404, "M008", "사용자를 찾을 수 없습니다."),
	UNAUTHORIZED(401, "M009", "권한이 없습니다."),

	// token
	INVALID_REFRESH_TOKEN(401, "T001", "유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요."),
	NOT_FOUND_REFRESH_TOKEN(500, "T002", "리프레시 토큰을 찾을 수 없습니다. 다시 로그인해주세요."),

	// external
	NETWORK_ERROR(500, "E001", "네트워크 에러입니다."),
	SERVICE_UNAVAILABLE(503, "E002", "서비스를 사용할 수 없습니다."),

	// episodes
	NOT_FOUND_EPISODE(404, "EP001", "해당 에피소드를 찾을 수 없습니다.");

	private int status;
	private final String code;
	private final String message;
}
