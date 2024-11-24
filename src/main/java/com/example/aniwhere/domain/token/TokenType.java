package com.example.aniwhere.domain.token;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum TokenType {
	ACCESS_TOKEN("accessToken", "AT:", Duration.ofMinutes(30)),
	REFRESH_TOKEN("refreshToken", "RT:", Duration.ofDays(7)),
	OAUTH_ACCESS_TOKEN("oAuthAccessToken", "OAT:", Duration.ofMinutes(30)),
	OAUTH_REFRESH_TOKEN("oAuthRefreshToken", "ORT:", Duration.ofDays(7)),
	BLACKLIST_ACCESS_TOKEN("blacklistAccessToken", "BAT:", Duration.ofMinutes(3)),
	BLACKLIST_REFRESH_TOKEN("blacklistRefreshToken", "BRT:", Duration.ofMinutes(3));

	private final String description;
	private final String prefix;
	private final Duration duration;

	TokenType(String description, String prefix, Duration duration) {
		this.description = description;
		this.prefix = prefix;
		this.duration = duration;
	}
}
