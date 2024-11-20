package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

/**
 * 유저 관련 예외를 처리하기 위한 클래스
 */
@Getter
public class UserException extends BusinessException {

	public UserException(ErrorCode errorCode) {
		super(errorCode);
	}
}
