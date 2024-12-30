package com.example.aniwhere.application.config.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

@Configuration
@Slf4j
public class CookieConfig {

	@Value("${jwt.access_token_expiration_time}")
	private long accessTokenExpirationTime;

	@Value("${jwt.refresh_token_expiration_time}")
	private long refreshTokenExpirationTime;

	public ResponseCookie createAccessTokenCookie(String name, String value) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.maxAge(accessTokenExpirationTime / 1000)
				.path("/")
				.build();
	}

	public ResponseCookie createRefreshTokenCookie(String name, String value) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.maxAge(refreshTokenExpirationTime / 1000)
				.path("/")
				.build();
	}

	public String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null || cookies.length == 0) {
			return null;
		}

		return Arrays.stream(cookies)
				.filter(cookie -> cookie.getName().equals(cookieName))
				.map(Cookie::getValue)
				.findFirst()
				.orElse(null);
	}

	public String extractAccessToken(HttpServletRequest request) {
		return extractTokenFromCookie(request, "access_token");
	}

	public String extractRefreshToken(HttpServletRequest request) {
		return extractTokenFromCookie(request, "refresh_token");
	}

	public ResponseCookie invalidateAccessTokenCookie(String name, String value) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.maxAge(0)
				.path("/")
				.build();
	}

	public ResponseCookie invalidateRefreshTokenCookie(String name, String value) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.maxAge(0)
				.path("/")
				.build();
	}

	public void invalidateAuthCookies(HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, invalidateAccessTokenCookie("access_token", "").toString());
		response.addHeader(HttpHeaders.SET_COOKIE, invalidateRefreshTokenCookie("refresh_token", "").toString());
		log.info("만료");
	}
}
