package com.example.aniwhere.controller.token;

import com.example.aniwhere.service.user.KakaoService;
import com.example.aniwhere.service.token.TokenService;
import com.example.aniwhere.domain.token.dto.OAuthToken;
import com.example.aniwhere.global.error.ErrorResponse;
import com.example.aniwhere.application.config.CookieConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Token", description = "토큰 관련 API")
@RequiredArgsConstructor
public class TokenApiController {

	private final TokenService tokenService;
	private final CookieConfig cookieConfig;
	private final KakaoService kakaoService;

	@Operation(
			summary = "액세스 토큰 재발급 (스프링 시큐리티)",
			description = "만료된 액세스 토큰을 재발급받기 위한 API - 스프링 시큐리티 자체 서비스"
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "토큰 재발급 성공",
					content = @Content(
							mediaType = "text/plain",
							schema = @Schema(type = "string"),
							examples = @ExampleObject(value = "리프레시 토큰을 기반으로 새로운 액세스 토큰 재발급에 성공하셨습니다.")
					),
					headers = @Header(
							name = HttpHeaders.SET_COOKIE,
							description = "새로 발급된 액세스 토큰",
							schema = @Schema(type = "string")
					)
			),
			@ApiResponse(
					responseCode = "500",
					description = "리프레시 토큰 오류",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 500,
                    "code": "T001",
                    "message": "유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요.",
                    "errors": []
                }
                """
							)
					)
			)
	})
	@PostMapping("/reissue")
	public ResponseEntity<String> createNewAccessToken(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = cookieConfig.resolveRefreshTokenInfo(request);
		String newAccessToken = tokenService.createNewAccessToken(refreshToken);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, newAccessToken.toString())
				.body("리프레시 토큰을 기반으로 새로운 액세스 토큰 재발급에 성공하셨습니다.");
	}

	@Operation(
			summary = "액세스 토큰 / 리프레시 토큰 재발급 (카카오)",
			description = "액세스 토큰과 리프레시 토큰을 재발급받기 위한 API - 카카오 소셜 로그인 서비스"
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "토큰 재발급 성공",
					content = @Content(
							mediaType = "text/plain",
							schema = @Schema(type = "string"),
							examples = @ExampleObject(value = "카카오 액세스 토큰 / 리프레시 토큰 재발급에 성공하셨습니다.")
					),
					headers = @Header(
							name = HttpHeaders.SET_COOKIE,
							description = "새로 발급된 카카오 액세스 토큰",
							schema = @Schema(
									type = "string",
									example = "kakao_access_token=eyJ...; Path=/; HttpOnly; Secure; SameSite=Strict"
							)
					)
			),
			@ApiResponse(
					responseCode = "500",
					description = "리프레시 토큰 오류",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 500,
                    "code": "T002",
                    "message": "리프레시 토큰을 찾을 수 없습니다. 다시 로그인해주세요.",
                    "errors": []
                }
                """
							)
					)
			),
			@ApiResponse(
					responseCode = "503",
					description = "카카오 서비스 연동 실패",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "status": 503,
                    "code": "E002",
                    "message": "서비스를 사용할 수 없습니다.",
                    "errors": []
                }
                """
							)
					)
			)
	})
	@GetMapping("/kakaoreissue")
	public ResponseEntity<String> createNewAccessTokenByKakao(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = cookieConfig.resolveRefreshTokenInfo(request);
		OAuthToken oAuthToken = kakaoService.kakaoReissue(refreshToken);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, oAuthToken.accessToken())
				.header(HttpHeaders.SET_COOKIE, oAuthToken.refreshToken())
				.body("카카오 액세스 토큰 / 카카오 리프레시 토큰 재발급에 성공하셨습니다.");
	}
}
