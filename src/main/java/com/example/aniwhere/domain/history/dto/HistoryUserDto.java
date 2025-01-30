package com.example.aniwhere.domain.history.dto;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.Status;
import com.example.aniwhere.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record HistoryUserDto(

		Long senderId,
		List<Long> receiverId,
		String animeTitle,
		String nickname,
		Status status,
		LocalDateTime createdAt
) {

	public List<History> toEntity(User sender, List<User> receivers) {
		return receivers.stream()
				.map(receiver -> getHistory(sender, receiver))
				.collect(Collectors.toList());
	}

	private History getHistory(User sender, User receiver) {
		return History.builder()
				.sender(sender)
				.receiver(receiver)
				.animeTitle(animeTitle)
				.status(status)
				.build();
	}
}
