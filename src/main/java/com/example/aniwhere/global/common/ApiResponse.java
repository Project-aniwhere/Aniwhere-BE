package com.example.aniwhere.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse {

	private int code;
	private String message;

	public static ApiResponse of(int code, String message) {
		return new ApiResponse(code, message);
	}

	public ApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
