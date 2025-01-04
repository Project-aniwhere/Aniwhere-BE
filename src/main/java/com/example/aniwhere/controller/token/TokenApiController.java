package com.example.aniwhere.controller.token;

import com.example.aniwhere.application.auth.kakao.KakaoApi;
import com.example.aniwhere.application.auth.kakao.dto.KakaoRenewalResponse;
import com.example.aniwhere.application.auth.resolver.LoginUser;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.service.token.TokenService;
import com.example.aniwhere.application.config.cookie.CookieConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Token", description = "토큰 관련 API")
@RequiredArgsConstructor
public class TokenApiController {

	private final TokenService tokenService;
	private final CookieConfig cookieConfig;
	private final KakaoApi kakaoApi;
	private final UserRepository userRepository;
	private final RedisService redisService;

	@Operation(
			summary = "액세스 토큰 재발급 (스프링 시큐리티)",
			description = "만료된 액세스 토큰을 재발급받기 위한 API - 스프링 시큐리티 자체 서비스"
	)
	@PostMapping("/reissue")
	public ResponseEntity<ResponseCookie> createNewAccessToken(HttpServletRequest request) {

		String refreshToken = cookieConfig.extractRefreshToken(request);
		ResponseCookie newAccessToken = tokenService.createNewAccessToken(refreshToken);

		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.SET_COOKIE, newAccessToken.toString())
				.build();
	}

	@Operation(
			summary = "카카오 액세스 토큰 재발급",
			description = "만료된 카카오 토큰을 재발급받기 위한 API - 카카오"
	)
	@PostMapping("/kakao/reissue")
	public ResponseEntity<KakaoRenewalResponse> kakaoReissue(@LoginUser final Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		String refreshToken = redisService.getOAuthRefreshToken(user.getEmail());
		KakaoRenewalResponse kakaoRenewalResponse = kakaoApi.renewalToken(refreshToken);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(kakaoRenewalResponse);
	}
}
