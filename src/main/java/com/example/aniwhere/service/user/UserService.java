package com.example.aniwhere.service.user;

import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.repository.UserRepository;
import com.example.aniwhere.application.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;

import static com.example.aniwhere.domain.token.TokenType.REFRESH_TOKEN;
import static com.example.aniwhere.domain.user.dto.UserDTO.*;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private static final String SELF_PROVIDER = "self";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;
	private final TokenProvider tokenProvider;
	private final EmailVerificationService emailVerificationService;
	private final CookieConfig cookieConfig;

	/**
	 * 스프링 시큐리티 기반의 회원가입
	 	* 요청 검증 -> 입력한 이메일의 계정이 존재하는지를 검증
	 	* 레디스에 인증 코드를 저장하는 방식은 그대로 가져가되 이 역시 유효 코드 만료 시간인 5분으로 그대로 설정
	    * 개선 : 이메일 인증 결과를 활용 + DTO에서 끼기 애매한 인증 코드 필드를 제거할 수 있음
	 * @param request
	 * @return User
	 */
	@Transactional
	public User signup(UserSignUpRequest request) {
		validateSignupRequest(request);

		User newUser = createUser(request);
		User savedUser = userRepository.save(newUser);
		emailVerificationService.deleteVerificationResult(request.getEmail());
		return savedUser;
	}

	private void validateSignupRequest(UserSignUpRequest request) {
		checkDuplicateEmail(request);
		validateEmailVerification(request.getEmail());
	}

	private void checkDuplicateEmail(UserSignUpRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserException(DUPLICATED_EMAIL);
		}
	}

	private void validateEmailVerification(String email) {
		if (!emailVerificationService.isEmailVerified(email)) {
			throw new UserException(EMAIL_VERIFICATION_FAIL);
		}
	}

	/**
	 * 스프링 시큐리티 기반의 로그인
	 * @param request
	 * @return List<ResponseCookie>
	 */
	@Transactional
	public List<ResponseCookie> signin(UserSignInRequest request) {

		User user = findUserByEmail(request.getEmail());

		if (!isValidPassword(request.getPassword(), user.getPassword())) {
			throw new UserException(PASSWORD_MISMATCH);
		}

		JwtToken jwtToken = tokenProvider.generateJwtToken(user);
		redisService.saveToken(user.getEmail(), Map.of(REFRESH_TOKEN, jwtToken.refreshToken()));
		return createTokenCookies(jwtToken);
	}


	private User createUser(UserSignUpRequest request) {
		return User.builder()
				.nickname(request.getNickname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.birthday(request.getBirthday())
				.birthyear(request.getBirthyear())
				.sex(request.getSex())
				.provider(SELF_PROVIDER)
				.providerId(SELF_PROVIDER)
				.role(request.getRole())
				.build();
	}

	private List<ResponseCookie> createTokenCookies(JwtToken token) {
		return List.of(
				cookieConfig.createAccessTokenCookie(token.accessToken()),
				cookieConfig.createRefreshTokenCookie(token.refreshToken())
		);
	}

	private boolean isValidPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}
}
