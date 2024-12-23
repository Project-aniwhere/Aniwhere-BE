package com.example.aniwhere.application.auth.jwt;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

public class WhitelistEntry {

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	private final String pattern;
	private final HttpMethod method;

	public WhitelistEntry(String pattern, HttpMethod method) {
		this.pattern = pattern;
		this.method = method;
	}

	public boolean matches(String path, String requestMethod) {
		return method.matches(requestMethod) && pathMatcher.match(pattern, path);
	}
}