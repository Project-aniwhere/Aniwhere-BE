package com.example.aniwhere.service.redis;

import com.example.aniwhere.domain.token.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.aniwhere.domain.token.TokenType.*;

@Slf4j
@Service
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private final ValueOperations<String, String> operations;

	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.operations = redisTemplate.opsForValue();
	}

	/**
	 * 레디스에 토큰을 저장
	 * @param email
	 * @param tokens
	 * @return void
	 */
	public void saveToken(String email, Map<TokenType, String> tokens) {
		tokens.forEach((key, value) ->
				operations.set(
						buildKey(key.getPrefix(), email),
						value,
						key.getDuration().toSeconds()
				));
	}

	/**
	 * 블랙리스트에 JWT 토큰을 저장, 그리고 리프레시 토큰을 삭제
	 * @param email
	 * @param tokens
	 * @return void
	 */
	public void saveBlackListJwtToken(String email, Map<TokenType, String> tokens) {
		tokens.forEach((tokenType, tokenValue) ->
				operations.set(
						buildKey(tokenType.getPrefix(), email),
						tokenValue,
						tokenType.getDuration().toSeconds()
				)
		);
		deleteToken(email, REFRESH_TOKEN);
	}

	/**
	 * SaveBlackListJwtToken 메서드 오버로딩
	 * @param email
	 * @param accessToken
	 * @param refreshToken
	 */
	public void saveBlackListJwtToken(String email, String accessToken, String refreshToken) {
		saveBlackListJwtToken(email, Map.of(ACCESS_TOKEN, accessToken, REFRESH_TOKEN, refreshToken));
	}

	/**
	 * OAuth2 토큰(사용자 액세스 토큰/사용자 리프레시 토큰)을 저장
	 * @param email
	 * @param token
	 * @param type
	 * @return void
	 */
	public void saveOAuthToken(String email, String token, TokenType type) {
		operations.set(
				buildKey(type.getPrefix(), email),
				token,
				type.getDuration().toSeconds(),
				TimeUnit.SECONDS
		);
	}

	/**
	 * 레디스에 저장된 OAuth2 토큰을 조회
	 * @param email
	 * @param type
	 * @return String
	 */
	public String getOAuthToken(String email, TokenType type) {
		return getValue(buildKey(type.getPrefix(), email));
	}

	/**
	 * 레디스에 저장된 OAuth2 토큰을 삭제
	 * @param email
	 * @param type
	 * @return void
	 */
	public void deleteOAuthToken(String email, TokenType type) {
		deleteFromRedis(buildKey(type.getPrefix(), email));
	}

	/**
	 * 레디스에 저장된 토큰을 조회
	 * @param email
	 * @param type
	 * @return String
	 */
	public String getToken(String email, TokenType type) {
		return getValue(buildKey(type.getPrefix(), email));
	}

	/**
	 * 레디스에 저장된 토큰을 삭제
	 * @param email
	 * @param type
	 * @return void
	 */
	public void deleteToken(String email, TokenType type) {
		deleteFromRedis(buildKey(type.getPrefix(), email));
	}

	public void setValue(String key, String value, Duration duration) {
		setValueWithExpiration(key, value, duration);
	}

	public String getValue(String key) {
		return getValueFromRedis(key);
	}

	public void deleteValue(String key) {
		deleteFromRedis(key);
	}

	private void setValueWithExpiration(String key, String value, Duration duration) {
		operations.set(key, value, duration);
	}

	private String getValueFromRedis(String key) {
		return operations.get(key);
	}

	private void deleteFromRedis(String key) {
		redisTemplate.delete(key);
	}

	private String buildKey(String prefix, String key) {
		return prefix + key;
	}
}