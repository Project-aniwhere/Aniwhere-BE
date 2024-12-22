package com.example.aniwhere.controller.user;

import com.example.aniwhere.domain.user.dto.UserSignInResult;
import com.example.aniwhere.service.user.UserService;
import com.example.aniwhere.application.config.CookieConfig;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthApiController {

	private final UserService userService;
	private final CookieConfig cookieConfig;

	@Operation(
			summary = "회원 가입",
			description = "새로운 사용자를 등록합니다."
	)
	@PostMapping("/auth/signup")
	public ResponseEntity<Void> signup(@Valid @RequestBody UserSignUpRequest request) {
		userService.signUp(request);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}

	@Operation(
			summary = "로그인",
			description = "이메일과 비밀번호로 로그인합니다."
	)
	@PostMapping("/auth/login")
	public ResponseEntity<UserSignInResponse> login(@Valid @RequestBody UserSignInRequest request, HttpServletResponse response) {
		UserSignInResult result = userService.signIn(request);
		addCookiesToResponse(response, result.getCookies());
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(result.getUserSignInResponse());
	}

	@Operation(
			summary = "로그아웃",
			description = "쿠키의 수명을 0으로 지정하여 무효화합니다."
	)
	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		cookieConfig.invalidateAuthCookies(response);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}

	private void addCookiesToResponse(HttpServletResponse response, List<ResponseCookie> cookies) {
		cookies.forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
		);
	}
}
