package com.example.aniwhere.service.token;

import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.RefreshToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.global.error.exception.TokenException;
import com.example.aniwhere.application.jwt.TokenProvider;
import com.example.aniwhere.repository.RefreshTokenRepository;
import com.example.aniwhere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.example.aniwhere.domain.token.TokenType.*;
import static com.example.aniwhere.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TokenService {

	private final TokenProvider tokenProvider;
	private final RedisService redisService;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * 쿠키 요청에 포함된 유효한 리프레시 토큰을 기반으로 새로운 액세스 토큰을 재발급
	 	* 리프레시 토큰의 유효성 검증
	 	* 리프레시 토큰의 이메일 정보를 기반으로 사용자 정보 조회
	 	* 레디스에 저장된 토큰이 유효하면서 쿠키 요청에 포함된 정보와 동일하다면 새로운 액세스 토큰을 생성하여 반환 -> Cache Hit 전략
	 	* TTL로 인해 소멸된 리프레시 토큰의 경우 -> Cache Miss 전략
	 * @param refreshToken
	 * @return String
	 */
	@Transactional
	public String createNewAccessToken(String refreshToken) {

		if (!tokenProvider.validateToken(refreshToken)) {
			throw new TokenException(INVALID_TOKEN, REFRESH_TOKEN);
		}

		String email = tokenProvider.getEmail(refreshToken);
		String storedRefreshToken = redisService.getToken(email, REFRESH_TOKEN);

		if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
			User user = getUserFromRefreshToken(refreshToken);
			String newAccessToken = tokenProvider.generateAccessToken(user);
			redisService.saveToken(email, Map.of(ACCESS_TOKEN, newAccessToken));
			return newAccessToken;
		}

		return handleCacheMiss(email);
	}

	/**
	 * 레디스에 리프레시 토큰이 없는 경우
	 	* TTL로 인해 리프레시 토큰이 없다면 DB로부터 조회
	 	* DB로부터 조회한 리프레시 토큰이 유효하지않다면 로그인을 유도
	    * DB로부터 조회한 리프레시 토큰이 유효하다면 새로운 액세스 토큰을 재발급해주고 조회한 리프레시 토큰을 레디스에 저장
	 * @param email
	 * @return String
	 */
	private String handleCacheMiss(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		RefreshToken dbRefreshToken = refreshTokenRepository.findByUserId(user.getId())
				.orElseThrow(() -> new TokenException(NOT_FOUND_REFRESH_TOKEN, REFRESH_TOKEN));

		boolean result = tokenProvider.validateToken(dbRefreshToken.toString());

		if (!result) {
			throw new TokenException(INVALID_REFRESH_TOKEN, REFRESH_TOKEN);
		}

		redisService.saveToken(email, Map.of(REFRESH_TOKEN, dbRefreshToken.toString()));
		return tokenProvider.generateAccessToken(user);
	}

	private User getUserFromRefreshToken(String refreshToken) {
		String email = tokenProvider.getEmail(refreshToken);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}
}
