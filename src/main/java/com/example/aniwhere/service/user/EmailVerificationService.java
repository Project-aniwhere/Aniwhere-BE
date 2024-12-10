package com.example.aniwhere.service.user;

import com.example.aniwhere.domain.user.dto.EmailVerificationRequest;
import com.example.aniwhere.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

	private static final String AUTH_CODE_PREFIX = "user:authCode:";
	private static final String EMAIL_VERIFICATION_PREFIX = "email:verified:";
	private static final int AUTH_CODE_LENGTH = 6;
	private static final String EMAIL_VERIFICATION_TITLE = "Aniwhere 이메일 2차 인증 코드 메일입니다.";

	private final RedisService redisService;
	private final EmailService emailService;

	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	/**
	 * 인증 코드 생성 및 발송
	 * @param email
	 */
	public void sendVerificationCode(String email) {
		String authCode = createAuthCode();
		emailService.sendEmail(email, EMAIL_VERIFICATION_TITLE, authCode);
		redisService.setValue(AUTH_CODE_PREFIX + email, authCode, getDuration(authCodeExpirationMillis));
	}

	/**
	 * 인증 코드 검증 및 결과 저장
	 * @param request
	 * @return
	 */
	public EmailVerificationResponse verifyCode(EmailVerificationRequest request) {
		String authCodeKey = buildAuthCodeKey(request.email());
		String savedCode = redisService.getValue(AUTH_CODE_PREFIX + request.email());

		// 인증 코드 만료 여부 검사
		if (savedCode == null) {
			return new EmailVerificationResponse("인증 시간이 만료되었거나 존재하지 않는 인증코드입니다.", false);
		}

		// 인증 코드 일치 여부 검사
		if (savedCode.equals(request.code())) {
			redisService.deleteValue(AUTH_CODE_PREFIX + request.email());
			redisService.setValue(EMAIL_VERIFICATION_PREFIX + request.email(), "true", getDuration(authCodeExpirationMillis));
			return new EmailVerificationResponse("인증 성공", true);
		}

		// 인증 코드 검증
		boolean isVerified = savedCode.equals(request.code());

		// 검증 결과 저장 및 응답 반환
		if (isVerified) {
			redisService.deleteValue(authCodeKey);
			saveVerificationResult(request.email(), true);
			log.info("이메일 인증 성공 - 이메일: {}", request.email());
			return new EmailVerificationResponse("인증 성공", true);
		} else {
			saveVerificationResult(request.email(), false);
			log.info("이메일 인증 실패 - 이메일: {}", request.email());
			return new EmailVerificationResponse("인증 실패", false);
		}
	}

	/**
	 * 인증 결과 저장
	 */
	private void saveVerificationResult(String email, boolean isVerified) {
		String verificationKey = buildVerificationKey(email);
		redisService.setValue(verificationKey, String.valueOf(isVerified), getDuration(authCodeExpirationMillis));
	}

	/**
	 * 이메일 인증 상태 확인
	 * @param email
	 * @return
	 */
	public boolean isEmailVerified(String email) {
		String verificationKey = buildVerificationKey(email);
		String result = redisService.getValue(verificationKey);
		return "true".equals(result);
	}

	/**
	 * 인증 결과 삭제
	 * @param email
	 */
	public void deleteVerificationResult(String email) {
		String verificationKey = buildVerificationKey(email);
		redisService.deleteValue(verificationKey);
		log.info("인증 결과 삭제 완료 - 이메일: {}", email);
	}


	/**
	 * 인증 코드 생성
	 * @return
	 */
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

	private Duration getDuration(long millis) {
		return Duration.ofMillis(millis);
	}

	private String buildAuthCodeKey(String email) {
		return AUTH_CODE_PREFIX + email;
	}

	private String buildVerificationKey(String email) {
		return EMAIL_VERIFICATION_PREFIX + email;
	}
}
