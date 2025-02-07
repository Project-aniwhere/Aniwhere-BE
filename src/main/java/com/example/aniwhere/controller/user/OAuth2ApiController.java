package com.example.aniwhere.controller.user;

import com.example.aniwhere.application.auth.kakao.KakaoApi;
import com.example.aniwhere.application.auth.kakao.WebClientKakaoCaller;
import com.example.aniwhere.application.auth.kakao.dto.AccessTokenRequest;
import com.example.aniwhere.application.auth.kakao.dto.KakaoProfileResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoTokenResponse;
import com.example.aniwhere.domain.user.dto.UserSignInResult;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.service.user.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OAuth2ApiController {

	private final RedisService redisService;
	private final KakaoService kakaoService;
	private final KakaoApi kakaoApi;

	@Operation(
			summary = "카카오 로그인 콜백",
			description = "카카오 로그인 콜백을 처리한다."
	)
	@PostMapping("/auth/kakao/callback")
	public ResponseEntity<UserSignInResponse> callback(@RequestParam(name = "code") String code,
													   HttpServletResponse response) {

		KakaoTokenResponse tokenResponse = kakaoApi.getToken(code);
		KakaoProfileResponse profileInfo = kakaoApi.getProfileInfo(tokenResponse.getAccess_token());

		UserSignInResult signInResult = kakaoService.loginKakaoUser(profileInfo, tokenResponse.getAccess_token());
		redisService.saveOAuthAccessToken(profileInfo.getKakao_account().getEmail(), tokenResponse.getAccess_token());
		redisService.saveOAuthRefreshToken(profileInfo.getKakao_account().getEmail(), tokenResponse.getRefresh_token());

		signInResult.getCookies().forEach(cookie ->
				response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()));

		return ResponseEntity.ok(signInResult.getUserSignInResponse());
	}

	@Operation(
			summary = "카카오 로그아웃",
			description = "카카오 액세스 토큰을 만료시킨다."
	)
	@PostMapping("/auth/kakao/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		kakaoService.logoutKakaoUser(response);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}
}
