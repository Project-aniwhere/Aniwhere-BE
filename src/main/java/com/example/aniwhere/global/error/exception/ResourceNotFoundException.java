package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;

public class ResourceNotFoundException extends BusinessException{

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
