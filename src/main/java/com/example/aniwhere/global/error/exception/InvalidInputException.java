package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;

public class InvalidInputException extends RuntimeException{
    private ErrorCode errorCode;

    public InvalidInputException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
