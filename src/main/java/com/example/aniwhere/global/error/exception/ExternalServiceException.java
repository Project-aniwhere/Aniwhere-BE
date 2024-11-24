package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

/**
 * 외부 API 호출 시 예외를 처리
 */
@Getter
public class ExternalServiceException extends BusinessException{

	private String url;
	private String message;

	public ExternalServiceException(ErrorCode errorCode, String url) {
		super(errorCode);
		this.url = url;
	}

	public ExternalServiceException(ErrorCode errorCode, String url, String message) {
		super(errorCode);
		this.url = url;
		this.message = message;
	}
}
