package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

/**
 * 토큰 관련 예외를 처리하기 위한 클래스
 */
@Getter
public class TokenException extends BusinessException {

	public TokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}