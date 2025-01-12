package com.example.aniwhere.service.redis;

import com.example.aniwhere.application.auth.jwt.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class RedisService {

	private final JwtProperties jwtProperties;
	private final RedisTemplate<String, String> redisTemplate;
	private final ValueOperations<String, String> operations;

	private static final String REFRESH_TOKEN_PREFIX = "RT:";
	private static final String OAUTH_ACCESS_PREFIX = "OAT:";
	private static final String OAUTH_REFRESH_PREFIX = "ORT:";
	private static final String BLACKLIST_ACCESS_PREFIX = "BAL:";
	private static final String BLACKLIST_REFRESH_PREFIX = "BRL";
	private static final String CODE_PREFIX = "CODE:";

	public RedisService(JwtProperties jwtProperties, RedisTemplate<String, String> redisTemplate) {
		this.jwtProperties = jwtProperties;
		this.redisTemplate = redisTemplate;
		this.operations = redisTemplate.opsForValue();
	}

	public void saveRefreshToken(Long userId, String token) {
		operations.set(REFRESH_TOKEN_PREFIX + userId, token, Duration.ofSeconds(jwtProperties.getRefresh_token_expiration_time()));
	}

	public String getRefreshToken(String userId) {
		return operations.get(REFRESH_TOKEN_PREFIX + userId);
	}

	public String getOAuthAccessToken(String email) {
		return operations.get(OAUTH_ACCESS_PREFIX + email);
	}

	public String getOAuthRefreshToken(String email) {
		return operations.get(OAUTH_REFRESH_PREFIX + email);
	}

	public void saveOAuthAccessToken(String email, String token) {
		operations.set(OAUTH_ACCESS_PREFIX + email, token, Duration.ofSeconds(21600)); // 6시간
	}

	public void saveOAuthRefreshToken(String email, String token) {
		operations.set(OAUTH_REFRESH_PREFIX + email, token, Duration.ofSeconds(5184000)); // 60일
	}

	public void deleteOAuthToken(String email) {
		redisTemplate.delete(OAUTH_ACCESS_PREFIX + email);
	}

	public void saveCode(String key, String value, Duration duration) {
		redisTemplate.opsForValue()
				.set(CODE_PREFIX + key, value, duration);
	}

	public void deleteCode(String key) {
		redisTemplate.delete(CODE_PREFIX + key);
	}

	public String getCode(String key) {
		return redisTemplate.opsForValue()
				.get(CODE_PREFIX + key);
	}

	public void saveBlackListAccessToken(String email, String token) {
		operations.set(BLACKLIST_ACCESS_PREFIX + email, token, Duration.ofSeconds(jwtProperties.getAccess_token_expiration_time()));
	}

	public void saveBlackListRefreshToken(String email, String token) {
		operations.set(BLACKLIST_REFRESH_PREFIX + email, token, Duration.ofSeconds(jwtProperties.getRefresh_token_expiration_time()));
	}
}