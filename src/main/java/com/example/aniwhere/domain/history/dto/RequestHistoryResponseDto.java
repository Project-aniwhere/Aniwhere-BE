package com.example.aniwhere.domain.history.dto;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.Status;

import java.time.LocalDateTime;

public record RequestHistoryResponseDto(
		Long id,
		HistoryUserResponseDto sender,
		HistoryUserResponseDto receiver,
		String animeTitle,
		Status status,
		String reply,
		LocalDateTime approvedAt
) {
	public static RequestHistoryResponseDto from(History history) {
		return new RequestHistoryResponseDto(
				history.getId(),
				HistoryUserResponseDto.from(history.getSender()),
				HistoryUserResponseDto.from(history.getReceiver()),
				history.getAnimeTitle(),
				history.getStatus(),
				history.getReply(),
				history.getApproved_at()
		);
	}
}
