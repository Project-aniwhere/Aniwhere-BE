package com.example.aniwhere.controller.mail;

import com.example.aniwhere.global.common.ApiResponse;
import com.example.aniwhere.service.user.EmailVerificationService;
import com.example.aniwhere.domain.user.dto.EmailAuthenticationRequest;
import com.example.aniwhere.domain.user.dto.EmailVerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Mail", description = "메일 인증 관련 API")
public class MailApiController {

	private final EmailVerificationService emailVerificationService;

	@Operation(
			summary = "이메일 인증 코드 발송",
			description = "회원가입을 위한 이메일 인증 코드를 발송합니다. 유효한 이메일 형식이어야 합니다."
	)
	@PostMapping("/auth/email/verifications-requests")
	public ResponseEntity<ApiResponse> sendMessage(@RequestBody EmailAuthenticationRequest request) {
		emailVerificationService.sendVerificationCode(request.email());
		return ResponseEntity.status(200)
				.body(ApiResponse.of(200, "인증 코드 발송 완료"));
	}

	@Operation(
			summary = "이메일 인증 코드 검증",
			description = "사용자가 입력한 이메일 인증 코드의 유효성을 검증합니다."
	)
	@PostMapping("/auth/email/verifications")
	public ResponseEntity<ApiResponse> verificationEmail(@RequestBody EmailVerificationRequest request) {
		EmailVerificationResponse response = emailVerificationService.verifyCode(request);

		if (!response.isVerified()) {
			return ResponseEntity.status(400)
					.body(ApiResponse.of(400, response.getMessage()));
		}
		return ResponseEntity.ok(ApiResponse.of(200, response.getMessage()));
	}
}
