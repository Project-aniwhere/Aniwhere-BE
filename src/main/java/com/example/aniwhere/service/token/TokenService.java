package com.example.aniwhere.service.token;

import com.example.aniwhere.application.config.cookie.CookieConfig;
import com.example.aniwhere.application.auth.jwt.dto.Claims;
import com.example.aniwhere.application.auth.jwt.dto.CreateTokenCommand;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.global.error.exception.TokenException;
import com.example.aniwhere.application.auth.jwt.provider.TokenProvider;
import com.example.aniwhere.repository.user.UserRepository;
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

	@Transactional
	public ResponseCookie createNewAccessToken(String refreshToken) {
		try {
			Claims claims = tokenProvider.validateToken(refreshToken);
			String storedRefreshToken = redisService.getRefreshToken(String.valueOf(claims.userId()));

			if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
				User user = getUserByUserId(claims.userId());
				String newAccessToken = generateAccessToken(user);
				return cookieConfig.createAccessTokenCookie("access_token", newAccessToken);
			}

			throw new TokenException(NOT_FOUND_REFRESH_TOKEN);
		} catch (TokenException e) {
			log.error("Refresh token validation failed", e);
			throw new TokenException(INVALID_TOKEN);
		}
	}

	private User getUserByUserId(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}

	private String generateAccessToken(User user) {
		CreateTokenCommand command = new CreateTokenCommand(
				user.getId(),
				user.getRole()
		);
		return tokenProvider.generateAccessToken(command);
	}
}
