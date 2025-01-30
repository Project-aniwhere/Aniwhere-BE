package com.example.aniwhere.service.admin;

import com.example.aniwhere.domain.admin.dto.EvaluationResponseDto;
import com.example.aniwhere.domain.admin.dto.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

	@Transactional
	public EvaluationResponseDto processEvaluation(Status status, String comment) {
		return EvaluationResponseDto.of(status, comment);
	}
}
