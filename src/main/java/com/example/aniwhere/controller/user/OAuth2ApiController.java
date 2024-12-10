package com.example.aniwhere.controller.user;

import com.example.aniwhere.global.common.ApiResponse;
import com.example.aniwhere.service.user.KakaoService;
import com.example.aniwhere.application.config.CookieConfig;
import io.swagger.v3.oas.annotations.Operation;
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
	@PostMapping("/auth/kakao/logout")
	public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
		String accessToken = cookieConfig.resolveAccessTokenInfo(request);
		String refreshToken = cookieConfig.resolveRefreshTokenInfo(request);

		kakaoService.kakaoLogout(accessToken, refreshToken);
		return ResponseEntity.status(200)
				.body(ApiResponse.of(200, "카카오 계정 로그아웃 완료"));
	}
}
