package com.example.aniwhere.global.error;

import lombok.*;

/**
 * 일관성있는 예외 응답을 반환하기 위한 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private String code;
	private String message;

	private ErrorResponse(final ErrorCode code) {
		this.code = code.getCode();
		this.message = code.getMessage();
	}

	public static ErrorResponse of(final ErrorCode code) {
		return new ErrorResponse(code);
	}
}