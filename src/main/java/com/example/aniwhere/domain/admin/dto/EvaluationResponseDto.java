package com.example.aniwhere.domain.admin.dto;

public record EvaluationResponseDto(Status status, String message) {

	public static EvaluationResponseDto of(Status status, String message) {
		return new EvaluationResponseDto(status, message);
	}
}
