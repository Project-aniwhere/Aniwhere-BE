package com.example.aniwhere.domain.admin.dto;

public record EvaluationRequestDto(Long historyId, Long userId, Status status, String message) {
}
