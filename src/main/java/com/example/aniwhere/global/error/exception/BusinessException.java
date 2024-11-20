package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import static com.example.aniwhere.global.error.ErrorResponse.*;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 비즈니스 로직 예외를 처리하기 위한 추상 클래스
 */
@Getter
public abstract class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;
	private List<FieldError> errors = new ArrayList<>();

	protected BusinessException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	protected BusinessException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	protected BusinessException(ErrorCode errorCode, List<FieldError> errors) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.errors = errors;
	}
}
