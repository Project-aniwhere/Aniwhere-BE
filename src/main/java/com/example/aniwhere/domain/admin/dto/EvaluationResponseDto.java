package com.example.aniwhere.domain.admin.dto;

public record EvaluationResponseDto(Long userId, Status status, String message) {

	public static EvaluationResponseDto of(Long userId, Status status, String message) {
		return new EvaluationResponseDto(userId, status, message);
	}
}
