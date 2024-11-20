package com.example.aniwhere.controller.user;

import com.example.aniwhere.service.user.KakaoService;
import com.example.aniwhere.global.error.ErrorResponse;
import com.example.aniwhere.application.config.CookieConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "OAuth2", description = "OAuth2 소셜 로그아웃 API")
public class OAuth2ApiController {

	private final KakaoService kakaoService;
	private final CookieConfig cookieConfig;

	@Operation(
			summary = "카카오 소셜 로그인 계정에 대한 로그아웃",
			description = "카카오 계정 로그아웃을 처리하고 관련된 액세스 토큰과 리프레시 토큰을 무효화합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "로그아웃 성공",
					content = @Content(
							mediaType = "text/plain",
							schema = @Schema(type = "string"),
							examples = @ExampleObject(value = "로그아웃이 성공적으로 처리되었습니다.")
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "유효하지 않은 토큰",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 400,
                    "code": "M001",
                    "message": "유효하지 않은 토큰입니다.",
                    "errors": []
                }
                """
							)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 요청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 401,
                    "code": "M009",
                    "message": "권한이 없습니다.",
                    "errors": []
                }
                """
							)
					)
			)
	})
	@GetMapping("/auth/kakao/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		String accessToken = cookieConfig.resolveAccessTokenInfo(request);
		String refreshToken = cookieConfig.resolveRefreshTokenInfo(request);

		kakaoService.kakaoLogout(accessToken, refreshToken);
		return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
	}
}
