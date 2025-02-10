package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotificationException extends BusinessException {

	public NotificationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
