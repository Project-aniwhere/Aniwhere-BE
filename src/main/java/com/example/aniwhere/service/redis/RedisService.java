package com.example.aniwhere.service.redis;

import com.example.aniwhere.application.jwt.JwtProperties;
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

	public RedisService(JwtProperties jwtProperties, RedisTemplate<String, String> redisTemplate) {
		this.jwtProperties = jwtProperties;
		this.redisTemplate = redisTemplate;
		this.operations = redisTemplate.opsForValue();
	}

	public void saveRefreshToken(String email, String token) {
		operations.set(email, token, Duration.ofSeconds(jwtProperties.getRefresh_token_expiration_time()));
	}

	public boolean deleteRefreshToken(String email) {
		Boolean result = redisTemplate.delete(email);
		return Boolean.TRUE.equals(result);
	}

	public String getRefreshToken(String email) {
		return operations.get(email);
	}

	public String getOAuthAccessToken(String email) {
		return operations.get(email);
	}

	public String getOAuthRefreshToken(String email) {
		return operations.get(email);
	}

	public void saveOAuthAccessToken(String email, String token) {
		operations.set("OAT:" + email, token, Duration.ofSeconds(jwtProperties.getAccess_token_expiration_time()));
	}

	public void saveOAuthRefreshToken(String email, String token) {
		operations.set("ORT:" + email, token, Duration.ofSeconds(jwtProperties.getRefresh_token_expiration_time()));
	}

	public void deleteOAuthToken(String email) {
		redisTemplate.delete(email);
	}

	public void saveCode(String key, String value, Duration duration) {
		redisTemplate.opsForValue()
				.set(key, value, duration);
	}

	public void deleteCode(String key) {
		redisTemplate.delete(key);
	}

	public String getCode(String key) {
		return redisTemplate.opsForValue()
				.get(key);
	}

	public void saveBlackListAccessToken(String email, String token) {
		operations.set(email, token, Duration.ofSeconds(jwtProperties.getAccess_token_expiration_time()));
	}

	public void saveBlackListRefreshToken(String email, String token) {
		operations.set(email, token, Duration.ofSeconds(jwtProperties.getRefresh_token_expiration_time()));
	}
}