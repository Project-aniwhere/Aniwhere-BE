package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;

public class ResourceNotFoundException extends RuntimeException{
    private ErrorCode errorCode;

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
