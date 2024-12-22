package com.example.aniwhere.service.user;

import com.example.aniwhere.domain.user.dto.UserSignInResult;
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


import java.util.Arrays;
import java.util.List;

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

	@Transactional
	public User signUp(UserSignUpRequest request) {
		validateSignupRequest(request);

		User newUser = createUser(request);
		User savedUser = userRepository.save(newUser);
		emailVerificationService.deleteVerificationResult(request.getEmail());
		return savedUser;
	}

	private void validateSignupRequest(UserSignUpRequest request) {
		checkDuplicateEmail(request);
		checkDuplicateNickname(request);
		validateEmailVerification(request.getEmail());
	}

	private void checkDuplicateEmail(UserSignUpRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserException(DUPLICATED_EMAIL);
		}
	}

	private void checkDuplicateNickname(UserSignUpRequest request) {
		if (userRepository.existsByNickname(request.getNickname())) {
			throw new UserException(DUPLICATED_NICKNAME);
		}
	}

	private void validateEmailVerification(String email) {
		if (!emailVerificationService.isEmailVerified(email)) {
			throw new UserException(EMAIL_VERIFICATION_FAIL);
		}
	}

	@Transactional
	public UserSignInResult signIn(UserSignInRequest request) {

		User user = findUserByEmail(request.getEmail());

		if (!isValidPassword(request.getPassword(), user.getPassword())) {
			throw new UserException(PASSWORD_MISMATCH);
		}

		JwtToken jwtToken = tokenProvider.generateJwtToken(user);
		redisService.saveRefreshToken(user.getEmail(), jwtToken.refreshToken());
		ResponseCookie accessTokenCookie = cookieConfig.createAccessTokenCookie("access_token", jwtToken.accessToken());
		ResponseCookie refreshTokenCookie = cookieConfig.createRefreshTokenCookie("refresh_token", jwtToken.refreshToken());

		List<ResponseCookie> cookies = Arrays.asList(accessTokenCookie, refreshTokenCookie);
		return UserSignInResult.of(user, cookies);
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

	private boolean isValidPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}
}
