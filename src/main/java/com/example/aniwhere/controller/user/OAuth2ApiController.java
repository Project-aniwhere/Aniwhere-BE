package com.example.aniwhere.controller.user;

import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.service.user.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OAuth2ApiController {

	private final KakaoService kakaoService;
	private final CookieConfig cookieConfig;

	@Operation(
			summary = "카카오 소셜 로그인 계정에 대한 로그아웃",
			description = "카카오 계정 로그아웃을 처리하고 관련된 액세스 토큰과 리프레시 토큰을 무효화합니다."
	)
	@PostMapping("/auth/kakao/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		String accessToken = cookieConfig.extractAccessToken(request);
		String refreshToken = cookieConfig.extractRefreshToken(request);

		kakaoService.kakaoLogout(accessToken, refreshToken);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}
}
