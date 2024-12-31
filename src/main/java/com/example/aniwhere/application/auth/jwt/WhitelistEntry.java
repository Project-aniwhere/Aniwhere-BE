package com.example.aniwhere.application.auth.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

@Slf4j
public class WhitelistEntry {

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	private final String pattern;
	private final HttpMethod method;

	public WhitelistEntry(String pattern, HttpMethod method) {
		this.pattern = pattern;
		this.method = method;
	}

	public boolean matches(String pattern, String method) {
		boolean pathMatch = pathMatcher.match(this.pattern, pattern);
		boolean methodMatch = this.method.matches(method);
		return pathMatch && methodMatch;
	}
}