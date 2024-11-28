package com.example.aniwhere.service.user;

import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.domain.user.dto.EmailVerificationRequest;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.repository.UserRepository;
import com.example.aniwhere.application.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.aniwhere.domain.token.TokenType.REFRESH_TOKEN;
import static com.example.aniwhere.domain.user.dto.UserDTO.*;
import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private static final String AUTH_CODE_PREFIX = "AuthCode: ";
	private static final String SELF_PROVIDER = "self";
	private static final int AUTH_CODE_LENGTH = 6;
	private static final String EMAIL_VERIFICATION_TITLE = "Aniwhere 이메일 2차 인증 코드 메일입니다.";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;
	private final TokenProvider tokenProvider;
	private final EmailService emailService;
	private final CookieConfig cookieConfig;

	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	/**
	 * 스프링 시큐리티 기반의 회원가입
	 	* 요청 검증 -> 입력한 이메일의 계정이 존재하는지를 검증
	 	* 요청이 올바르다면 인증 코드를 생성하여 레디스에 저장
	    	* 인증 코드가 레디스에 없다면 이메일 인증을 하지 않은 것으로 간주
	 		* 인증 코드가 레디스에 있다면 인증 코드 검증
	 * @param request
	 * @return User
	 */
	@Transactional
	public User signup(UserSignUpRequest request) {
		validateSignupRequest(request);

		User newUser = createUser(request);
		User savedUser = userRepository.save(newUser);

		String authCodeKey = buildAuthCodeKey(request.getEmail());
		redisService.deleteAuthCode(authCodeKey);
		return savedUser;
	}

	private void validateSignupRequest(UserSignUpRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserException(DUPLICATED_EMAIL);
		}

		String authCodeKey = buildAuthCodeKey(request.getEmail());
		validateEmailVerification(authCodeKey, request.getAuthCode());
	}

	private void validateEmailVerification(String authCodeKey, String submittedCode) {
		if (!redisService.hasAuthCode(authCodeKey)) {
			throw new UserException(EMAIL_VERIFICATION_FAIL);
		}

		String savedCode = redisService.getAuthCode(authCodeKey);
		if (!savedCode.equals(submittedCode)) {
			throw new UserException(VERIFICATION_CODE_MISMATCH);
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

	/**
	 * 이메일 인증 코드 발송
	 * @param toEmail
	 * @return void
	 */
	public void sendCodeToEmail(String toEmail) {
		this.checkDuplicatedEmail(toEmail);		// 우선적으로 이메일 중복 검사 체크

		String authCode = createAuthCode();
		String authCodeKey = buildAuthCodeKey(toEmail);
		emailService.sendEmail(toEmail, EMAIL_VERIFICATION_TITLE, authCode);
		redisService.saveAuthCode(authCodeKey, authCode, Duration.ofMillis(this.authCodeExpirationMillis));
	}

	/**
	 * 2차 인증 코드 일치 여부 검증
	 * @param request
	 * @return
	 */
	public EmailVerificationResponse verifiedCode(EmailVerificationRequest request) {
		String authCodeKey = buildAuthCodeKey(request.email());

		if (!redisService.hasAuthCode(authCodeKey)) {
			return new EmailVerificationResponse("인증 시간이 만료되었습니다. 인증 번호를 다시 요청해주세요.", false);
		}

		String savedCode = redisService.getAuthCode(authCodeKey);

		if (savedCode.equals(request.code())) {
			redisService.deleteAuthCode(authCodeKey);
			return new EmailVerificationResponse("인증이 완료되었습니다.", true);
		}

		return new EmailVerificationResponse("인증에 실패했습니다.", false);
	}

	private void checkDuplicatedEmail(String email) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new UserException(DUPLICATED_EMAIL);
		}
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

	private String buildAuthCodeKey(String email) {
		return AUTH_CODE_PREFIX + email;
	}

	private String createAuthCode() {
		try {
			Random random = SecureRandom.getInstanceStrong();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
				builder.append(random.nextInt(10));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			log.debug("랜덤 생성 오류");
			throw new RuntimeException("랜덤 생성 오류");
		}
	}
}
