package com.example.aniwhere.repository.pickedAnime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLockRepository {

	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Boolean lock(Long key) {
		return redisTemplate
				.opsForValue()
				.setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
	}

	public Boolean unlock(Long key) {
		return redisTemplate
				.delete(generateKey(key));
	}

	private String generateKey(Long key) {
		return key.toString();
	}

}
