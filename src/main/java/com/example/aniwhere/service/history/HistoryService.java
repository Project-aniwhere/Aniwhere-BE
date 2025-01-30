package com.example.aniwhere.service.history;

import com.example.aniwhere.domain.history.History;
import com.example.aniwhere.domain.history.dto.HistoryUserDto;
import com.example.aniwhere.domain.history.dto.RequestHistoryResponseDto;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.history.HistoryRepository;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.aniwhere.domain.user.Role.ROLE_ADMIN;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

	private final UserRepository userRepository;
	private final HistoryRepository historyRepository;

	@Transactional
	public List<RequestHistoryResponseDto> requestAnimeToAdmin(Long userId, HistoryUserDto request) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		List<User> admin = userRepository.findAllByRole(ROLE_ADMIN);
		List<History> histories = request.toEntity(user, admin);

		return historyRepository.saveAll(histories)
				.stream()
				.map(RequestHistoryResponseDto::from)
				.collect(Collectors.toList());
	}

}
