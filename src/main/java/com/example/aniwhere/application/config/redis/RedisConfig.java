package com.example.aniwhere.application.config.redis;

import com.example.aniwhere.domain.notification.dto.NotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisOperations<String, NotificationDto> eventRedisOperations(
			RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper
	) {
		Jackson2JsonRedisSerializer<NotificationDto> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(NotificationDto.class);
		jsonRedisSerializer.setObjectMapper(objectMapper);
		RedisTemplate<String, NotificationDto> eventRedisTemplate = new RedisTemplate<>();
		eventRedisTemplate.setConnectionFactory(redisConnectionFactory);
		eventRedisTemplate.setKeySerializer(RedisSerializer.string());
		eventRedisTemplate.setValueSerializer(jsonRedisSerializer);
		eventRedisTemplate.setHashKeySerializer(RedisSerializer.string());
		eventRedisTemplate.setHashValueSerializer(jsonRedisSerializer);
		return eventRedisTemplate;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		return redisMessageListenerContainer;
	}
}
