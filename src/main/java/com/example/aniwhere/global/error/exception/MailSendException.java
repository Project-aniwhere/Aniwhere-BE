package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

/**
 * 메일 전송 실패 시 처리하는 예외
 */
@Getter
public class MailSendException extends BusinessException {

	private final boolean isSend;

	public MailSendException(ErrorCode errorCode, boolean isSend) {
		super(errorCode);
		this.isSend = isSend;
	}
}
