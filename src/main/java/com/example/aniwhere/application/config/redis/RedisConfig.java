package com.example.aniwhere.application.config.redis;

import com.example.aniwhere.domain.notification.dto.NotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.sentinel.master}")
	private String master;

	@Value("${spring.data.redis.sentinel.nodes}")
	private String nodes;

	@Value("${spring.data.redis.password}")
	private String password;

	@Value("${spring.data.redis.timeout:3000}")
	private long timeout;

	@Bean
	public RedisConnectionFactory lettuceConnectionFactory() {
		RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration()
				.master(master);
		redisSentinelConfiguration.setPassword(password);

		String[] nodes = this.nodes.split(",");
		for (String node : nodes) {
			String[] hostAndPort = node.trim().split(":");
			redisSentinelConfiguration.sentinel(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
		}

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(timeout))
				.shutdownTimeout(Duration.ofMillis(timeout))
				.build();

		return new LettuceConnectionFactory(redisSentinelConfiguration, clientConfig);
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
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
