package com.example.aniwhere.domain.history.dto;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.ReplyStatus;

import java.time.LocalDateTime;

public record AdminHistoryResponseDto(
		Long historyId,
		Long senderId,
		String senderNickname,
		String content,
		ReplyStatus status,
		LocalDateTime approvedAt,
		String reply
) {

	public static AdminHistoryResponseDto from(History history) {
		return new AdminHistoryResponseDto(
                history.getId(),
                history.getSender().getId(),
                history.getSender().getNickname(),
                history.getContent(),
                history.getStatus(),
				history.getApprovedAt(),
                history.getReply()
        );
	}
}
