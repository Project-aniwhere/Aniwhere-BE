package com.example.aniwhere.service.user;

import com.example.aniwhere.application.auth.jwt.dto.CreateTokenCommand;
import com.example.aniwhere.domain.user.dto.UserDTO;
import com.example.aniwhere.domain.user.dto.UserSignInResult;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.application.config.cookie.CookieConfig;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.application.auth.jwt.provider.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;


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

		JwtToken jwtToken = generateTokens(user);
		redisService.saveRefreshToken(user.getId(), jwtToken.refreshToken());
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

	private JwtToken generateTokens(User user) {
		CreateTokenCommand command = new CreateTokenCommand(user.getId(), user.getRole());

		String accessToken = tokenProvider.generateAccessToken(command);
		String refreshToken = tokenProvider.generateRefreshToken(command, user);

		return new JwtToken(accessToken, refreshToken);
	}

	@Transactional(readOnly = true)
	public UserDTO.UserInfoResponse getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		return UserDTO.UserInfoResponse.from(user);
	}

	@Transactional(readOnly = true)
	public boolean isNicknameAvailable(String nickName) {
		return !userRepository.existsByNickname(nickName);
	}

	@Transactional
	public UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest updateRequest) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		User updatedUser = User.builder()
				.nickname(updateRequest.getNickname() != null && !updateRequest.getNickname().equals(user.getNickname())
						? validateAndUpdateNickname(updateRequest.getNickname())
						: user.getNickname())
				.email(updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())
						? validateAndUpdateEmail(updateRequest.getEmail())
						: user.getEmail())
				.password(updateRequest.getPassword() != null
						? passwordEncoder.encode(updateRequest.getPassword())
						: user.getPassword())
				.build();

		user.updateUserInfo(updatedUser);

		userRepository.save(user);
		return UserDTO.UserInfoResponse.from(user);
	}

	private String validateAndUpdateNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new UserException(DUPLICATED_NICKNAME);
		}
		return nickname;
	}

	private String validateAndUpdateEmail(String email) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new UserException(DUPLICATED_EMAIL);
		}
		return email;
	}
}
