package com.example.aniwhere.controller.history;

import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.history.dto.HistoryUserDto;
import com.example.aniwhere.service.history.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryApiController {

	private final HistoryService historyService;

	@Operation(
			summary = "[사용자용] 작품 요청",
			description = "관리자에게 작품 요청을 보낸다."
	)
	@PostMapping("/anime/sent")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<Void> sentAnime(@LoginUser final Long userId,
										  @RequestBody final HistoryUserDto dto) {
		historyService.requestAnimeToAdmin(userId, dto);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}
}
