package com.example.aniwhere.application.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtWhitelist {

	private final List<WhitelistEntry> whiteList;

	public JwtWhitelist() {
		this.whiteList = initializeWhitelist();
	}

	public boolean isWhitelisted(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		String requestMethod = request.getMethod();

		return whiteList.stream()
				.anyMatch(entry -> entry.matches(requestPath, requestMethod));
	}

	private List<WhitelistEntry> initializeWhitelist() {
		List<WhitelistEntry> entries = new ArrayList<>();

		// 기본 경로들
		entries.add(new WhitelistEntry("/error", HttpMethod.GET));
		entries.add(new WhitelistEntry("/favicon.ico", HttpMethod.GET));
		entries.add(new WhitelistEntry("/static/**", HttpMethod.GET));

		// 인증 관련 경로들
		entries.add(new WhitelistEntry("/api/auth/login", HttpMethod.POST));
		entries.add(new WhitelistEntry("/api/auth/signup", HttpMethod.POST));

		// 카카오 인증 관련 경로들 수정
		entries.add(new WhitelistEntry("/api/auth/kakao/callback", HttpMethod.GET));
		entries.add(new WhitelistEntry("/api/auth/kakao/login", HttpMethod.POST));
		entries.add(new WhitelistEntry("/oauth/authorize", HttpMethod.GET));

		// API 문서 관련
		entries.add(new WhitelistEntry("/api/v3/api-docs/**", HttpMethod.GET));
		entries.add(new WhitelistEntry("/api/swagger-ui/**", HttpMethod.GET));

		// 기타 허용 경로들
		entries.add(new WhitelistEntry("/api/anime/**", HttpMethod.GET));
		entries.add(new WhitelistEntry("/api/episodes/**", HttpMethod.GET));
		entries.add(new WhitelistEntry("/api/check/**", HttpMethod.GET));
		entries.add(new WhitelistEntry("/", HttpMethod.GET));

		return entries;
	}
}