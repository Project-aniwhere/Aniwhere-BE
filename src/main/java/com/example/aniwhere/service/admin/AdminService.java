package com.example.aniwhere.service.admin;

import com.example.aniwhere.domain.admin.dto.EvaluationRequestDto;
import com.example.aniwhere.domain.admin.dto.EvaluationResponseDto;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.service.notification.event.AdminResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.aniwhere.domain.admin.dto.Status.APPROVED;
import static com.example.aniwhere.domain.admin.dto.Status.REJECTED;
import static com.example.aniwhere.global.error.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public EvaluationResponseDto processEvaluation(EvaluationRequestDto dto) {

		User reply = userRepository.findById(dto.userId())
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		switch (dto.status()) {
			case APPROVED -> eventPublisher.publishEvent(new AdminResponseEvent(dto.historyId(), reply, APPROVED, "사용자 요청이 승인되었습니다."));
			case REJECTED -> eventPublisher.publishEvent(new AdminResponseEvent(dto.historyId(), reply, REJECTED, "사용자 요청이 반려되었습니다."));
		}

		return EvaluationResponseDto.of(dto.userId(), dto.status(), dto.message());
	}
}
