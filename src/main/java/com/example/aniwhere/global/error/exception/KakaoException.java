package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class KakaoException extends BusinessException{

    public KakaoException(ErrorCode errorCode) {
        super(errorCode);
    }
}
