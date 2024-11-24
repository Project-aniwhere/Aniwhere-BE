package com.example.aniwhere.controller.mail;

import com.example.aniwhere.service.user.UserService;
import com.example.aniwhere.domain.user.dto.EmailAuthenticationRequest;
import com.example.aniwhere.domain.user.dto.EmailVerificationRequest;
import com.example.aniwhere.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Mail", description = "메일 인증 관련 API")
public class MailApiController {

	private final UserService userService;

	@Operation(
			summary = "이메일 인증 코드 발송",
			description = "회원가입을 위한 이메일 인증 코드를 발송합니다. 유효한 이메일 형식이어야 합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "인증 코드 발송 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(
									type = "string",
									example = "인증 코드가 포함된 메일이 발송되었습니다."
							)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 이메일 형식",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(
									type = "string",
									example = "이메일 형식이 올바르지 않습니다."
							)
					)
			)
	})
	@PostMapping("/auth/email/verifications-requests")
	public ResponseEntity<String> sendMessage(@RequestBody EmailAuthenticationRequest request) {
		userService.sendCodeToEmail(request.email());
		return ResponseEntity.ok("인증 코드가 포함된 메일이 발송되었습니다.");
	}

	@Operation(
			summary = "이메일 인증 코드 검증",
			description = "사용자가 입력한 이메일 인증 코드의 유효성을 검증합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "이메일 인증 성공",
					content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청 (이메일 형식 오류 또는 인증 코드 누락)",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 400,
                    "code": "M005",
                    "message": "이메일 인증에 실패하셨습니다.",
                    "errors": [
                        {
                            "field": "email",
                            "value": "invalid-email",
                            "reason": "이메일 형식이 올바르지 않습니다."
                        }
                    ]
                }
                """
							)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "인증 코드 불일치",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 400,
                    "code": "M006",
                    "message": "인증 코드가 일치하지 않습니다.",
                    "errors": []
                }
                """
							)
					)
			)
	})
	@GetMapping("/auth/email/verifications")
	public ResponseEntity<EmailVerificationResponse> verificationEmail(@RequestBody EmailVerificationRequest request) {
		return ResponseEntity.ok(userService.verifiedCode(request));
	}
}
