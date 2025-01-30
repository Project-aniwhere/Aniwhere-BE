package com.example.aniwhere.domain.history.dto;

import com.example.aniwhere.domain.user.User;

// 사용자 요청 내역
public record HistoryUserResponseDto(
		Long id,
		String nickname
) {
	public static HistoryUserResponseDto from(User user) {
		return new HistoryUserResponseDto(user.getId(), user.getNickname());
	}
}
