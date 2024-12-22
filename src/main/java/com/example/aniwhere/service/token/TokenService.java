package com.example.aniwhere.service.token;

import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.RefreshToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.global.error.exception.TokenException;
import com.example.aniwhere.application.jwt.TokenProvider;
import com.example.aniwhere.repository.RefreshTokenRepository;
import com.example.aniwhere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.aniwhere.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class TokenService {

	private final TokenProvider tokenProvider;
	private final CookieConfig cookieConfig;
	private final RedisService redisService;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public ResponseCookie createNewAccessToken(String refreshToken) {

		if (!tokenProvider.validateToken(refreshToken)) {
			throw new TokenException(INVALID_TOKEN);
		}

		String email = tokenProvider.getEmail(refreshToken);
		String storedRefreshToken = redisService.getRefreshToken(email);

		if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
			User user = getUserFromRefreshToken(refreshToken);
			String newAccessToken = tokenProvider.generateAccessToken(user);
			return cookieConfig.createAccessTokenCookie("access_token", newAccessToken);
		}

		return handleCacheMiss(email);
	}

	private ResponseCookie handleCacheMiss(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		RefreshToken dbRefreshToken = refreshTokenRepository.findByUserId(user.getId())
				.orElseThrow(() -> new TokenException(NOT_FOUND_REFRESH_TOKEN));

		boolean result = tokenProvider.validateToken(dbRefreshToken.toString());

		if (!result) {
			throw new TokenException(INVALID_REFRESH_TOKEN);
		}

		redisService.saveRefreshToken(email, dbRefreshToken.getRefreshToken());
		String newAccessToken = tokenProvider.generateAccessToken(user);
		return cookieConfig.createAccessTokenCookie("access_token", newAccessToken);
	}

	private User getUserFromRefreshToken(String refreshToken) {
		String email = tokenProvider.getEmail(refreshToken);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}
}
