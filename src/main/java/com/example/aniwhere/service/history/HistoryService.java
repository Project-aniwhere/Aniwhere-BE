package com.example.aniwhere.service.history;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.dto.HistoryUserDto;
import com.example.aniwhere.domain.history.dto.RequestHistoryResponseDto;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.service.notification.event.UserRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.aniwhere.domain.user.Role.ROLE_ADMIN;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public RequestHistoryResponseDto requestAnime(Long userId, HistoryUserDto request) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		User admin = userRepository.findAllByRole(ROLE_ADMIN);
		History history = request.toEntity(user, admin);

		eventPublisher.publishEvent(new UserRequestEvent(user, admin, request.content()));
		return RequestHistoryResponseDto.from(history);
	}

}
