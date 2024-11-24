package com.example.aniwhere.controller.user;

import com.example.aniwhere.service.user.UserService;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "유저 관련 API")
@RequiredArgsConstructor
public class UserApiController {

	private final UserService userService;
	private final CookieConfig cookieConfig;

	@Operation(
			summary = "회원 가입",
			description = "새로운 사용자를 등록합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "회원가입 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = UserSignUpResponse.class),
							examples = @ExampleObject(
									value = """
                {
                    "id": 1,
                    "nickname": "testuser",
                    "email": "test@example.com",
                    "birthday": "0101",
                    "birthyear": "1990",
                    "sex": "MALE",
                    "role": "ROLE_USER"
                }
                """
							)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "유효성 검증 실패 또는 중복 데이터",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@ExampleObject(
											name = "필수 필드 누락",
											value = """
                    {
                        "status": 400,
                        "code": "C002",
                        "message": "invalid input type",
                        "errors": [
                            {
                                "field": "nickname",
                                "value": "",
                                "reason": "닉네임은 필수입니다."
                            },
                            {
                                "field": "email",
                                "value": "",
                                "reason": "이메일은 필수입니다."
                            },
                            {
                                "field": "password",
                                "value": "",
                                "reason": "비밀번호는 필수 입력 값입니다."
                            },
                            {
                                "field": "authCode",
                                "value": "",
                                "reason": "2차 인증 코드는 필수입니다."
                            }
                        ]
                    }
                    """
									),
									@ExampleObject(
											name = "잘못된 형식",
											value = """
                    {
                        "status": 400,
                        "code": "C002",
                        "message": "invalid input type",
                        "errors": [
                            {
                                "field": "email",
                                "value": "invalid-email",
                                "reason": "이메일 형식이 올바르지 않습니다."
                            },
                            {
                                "field": "birthyear",
                                "value": "19",
                                "reason": "생년은 4자리여야 합니다."
                            },
                            {
                                "field": "birthday",
                                "value": "1",
                                "reason": "출생일자는 4자리여야 합니다."
                            }
                        ]
                    }
                    """
									),
									@ExampleObject(
											name = "이메일 중복",
											value = """
                    {
                        "status": 400,
                        "code": "M004",
                        "message": "중복된 메일입니다.",
                        "errors": []
                    }
                    """
									),
									@ExampleObject(
											name = "닉네임 중복",
											value = """
                    {
                        "status": 400,
                        "code": "M003",
                        "message": "중복된 닉네임입니다.",
                        "errors": []
                    }
                    """
									),
									@ExampleObject(
											name = "인증코드 불일치",
											value = """
                    {
                        "status": 400,
                        "code": "M006",
                        "message": "인증 코드가 일치하지 않습니다.",
                        "errors": []
                    }
                    """
									)
							}
					)
			)
	})
	@PostMapping("/auth/signup")
	public ResponseEntity<UserSignUpResponse> signup(@Valid @RequestBody UserSignUpRequest request) {
		return ResponseEntity.ok(new UserSignUpResponse(userService.signup(request)));
	}

	@Operation(
			summary = "로그인",
			description = "이메일과 비밀번호로 로그인합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "로그인 성공",
					content = @Content(
							mediaType = "text/plain",
							schema = @Schema(type = "string"),
							examples = @ExampleObject(value = "성공적으로 로그인되었습니다.")
					),
					headers = {
							@Header(
									name = HttpHeaders.SET_COOKIE,
									description = "JWT 액세스 토큰",
									schema = @Schema(
											type = "string",
											example = "accessToken=eyJ...; Path=/; HttpOnly; Secure; SameSite=Strict"
									)
							),
							@Header(
									name = HttpHeaders.SET_COOKIE,
									description = "JWT 리프레시 토큰",
									schema = @Schema(
											type = "string",
											example = "refreshToken=eyJ...; Path=/; HttpOnly; Secure; SameSite=Strict"
									)
							)
					}
			),
			@ApiResponse(
					responseCode = "400",
					description = "유효성 검증 실패 또는 로그인 실패",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@ExampleObject(
											name = "잘못된 이메일 형식",
											value = """
                    {
                        "status": 400,
                        "code": "C002",
                        "message": "invalid input type",
                        "errors": [
                            {
                                "field": "email",
                                "value": "invalid-email",
                                "reason": "이메일 형식이 올바르지 않습니다."
                            }
                        ]
                    }
                    """
									),
									@ExampleObject(
											name = "필수 필드 누락",
											value = """
                    {
                        "status": 400,
                        "code": "C002",
                        "message": "invalid input type",
                        "errors": [
                            {
                                "field": "password",
                                "value": "",
                                "reason": "비밀번호는 필수 입력 값입니다."
                            }
                        ]
                    }
                    """
									),
									@ExampleObject(
											name = "인증 실패",
											value = """
                    {
                        "status": 400,
                        "code": "M002",
                        "message": "유효하지 않은 인증 정보입니다.",
                        "errors": []
                    }
                    """
									),
									@ExampleObject(
											name = "비밀번호 불일치",
											value = """
                    {
                        "status": 400,
                        "code": "M007",
                        "message": "비밀번호가 일치하지 않습니다.",
                        "errors": []
                    }
                    """
									)
							}
					)
			)
	})
	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserSignInRequest request, HttpServletResponse response) {

		List<ResponseCookie> responseCookies = userService.signin(request);
		responseCookies.forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()));

		return ResponseEntity.ok().body("성공적으로 로그인되었습니다.");
	}

	@Operation(
			summary = "로그아웃",
			description = "쿠키의 수명을 0으로 지정하여 무효화합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "로그아웃 성공",
					content = @Content(
							mediaType = "text/plain",
							schema = @Schema(type = "string"),
							examples = @ExampleObject(value = "로그아웃 되었습니다.")
					),
					headers = {
							@Header(
									name = HttpHeaders.SET_COOKIE,
									description = "만료된 액세스 토큰",
									schema = @Schema(
											type = "string",
											example = "accessToken=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Strict"
									)
							),
							@Header(
									name = HttpHeaders.SET_COOKIE,
									description = "만료된 리프레시 토큰",
									schema = @Schema(
											type = "string",
											example = "refreshToken=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Strict"
									)
							)
					}
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
	@PostMapping("/auth/logout")
	public ResponseEntity<String> logout() {

		ResponseCookie accessTokenCookie = cookieConfig.expireAccessTokenCookie();
		ResponseCookie refreshTokenCookie = cookieConfig.expireRefreshTokenCookie();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body("로그아웃 되었습니다.");
	}
}
