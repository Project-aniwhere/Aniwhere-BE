package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class HistoryException extends BusinessException {

	public HistoryException(ErrorCode errorCode) {
		super(errorCode);
	}
}
