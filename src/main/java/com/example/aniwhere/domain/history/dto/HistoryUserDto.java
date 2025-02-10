package com.example.aniwhere.domain.history.dto;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.ReplyStatus;
import com.example.aniwhere.domain.user.User;
import lombok.Builder;

public record HistoryUserDto(

		Long senderId,
		Long receiverId,
		String content,
		ReplyStatus status
) {

	@Builder
	public History toEntity(User sender, User receiver) {
		return History.builder()
				.sender(sender)
				.receiver(receiver)
				.status(status)
				.build();
	}
}
