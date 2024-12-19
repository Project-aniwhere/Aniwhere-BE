package com.example.aniwhere.controller.user;

import com.example.aniwhere.global.common.ApiResponse;
import com.example.aniwhere.service.user.UserService;
import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.service.user.validator.CheckNicknameValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "유저 관련 API")
@RequiredArgsConstructor
public class AuthApiController {

	private final CheckNicknameValidator checkNicknameValidator;
	private final UserService userService;
	private final CookieConfig cookieConfig;

	@InitBinder(value = "userSignUpRequest")	// 회원가입 시에만 적용되는 바인더
	protected void validatorBinder(WebDataBinder binder) {
		binder.addValidators(checkNicknameValidator);
	}

	@Operation(
			summary = "회원 가입",
			description = "새로운 사용자를 등록합니다."
	)
	@PostMapping("/auth/signup")
	public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserSignUpRequest request) {
		userService.signup(request);
		return ResponseEntity.status(201)
				.body(ApiResponse.of(201, "회원가입 처리 완료"));

	}

	@Operation(
			summary = "로그인",
			description = "이메일과 비밀번호로 로그인합니다."
	)
	@PostMapping("/auth/login")
	public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserSignInRequest request, HttpServletResponse response) {

		List<ResponseCookie> responseCookies = userService.signin(request);
		responseCookies.forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()));

		return ResponseEntity.ok()
				.body(ApiResponse.of(200, "로그인 처리 완료"));
	}

	@Operation(
			summary = "로그아웃",
			description = "쿠키의 수명을 0으로 지정하여 무효화합니다."
	)
	@PostMapping("/auth/logout")
	public ResponseEntity<ApiResponse> logout() {

		ResponseCookie accessTokenCookie = cookieConfig.expireAccessTokenCookie();
		System.out.println(accessTokenCookie);
		ResponseCookie refreshTokenCookie = cookieConfig.expireRefreshTokenCookie();
		System.out.println(refreshTokenCookie);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(ApiResponse.of(200, "로그아웃 처리 완료"));
	}
}
