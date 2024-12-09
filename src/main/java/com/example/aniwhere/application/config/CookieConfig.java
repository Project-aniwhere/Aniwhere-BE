package com.example.aniwhere.application.config;

import com.example.aniwhere.domain.token.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;

import static com.example.aniwhere.domain.token.TokenType.ACCESS_TOKEN;
import static com.example.aniwhere.domain.token.TokenType.REFRESH_TOKEN;

@Configuration
@Slf4j
public class CookieConfig {

	@Value("${jwt.access_token_expiration_time}")
	private long accessTokenExpirationTime;

	@Value("${jwt.refresh_token_expiration_time}")
	private long refreshTokenExpirationTime;

	/**
	 * 쿠키에 포함된 액세스 토큰 정보 추출
	 * @param request
	 * @return String
	 */
	public String resolveAccessTokenInfo(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (ACCESS_TOKEN.getDescription().equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 쿠키에 포함된 리프레시 토큰 정보 추출
	 * @param request
	 * @return String
	 */
	public String resolveRefreshTokenInfo(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (REFRESH_TOKEN.getDescription().equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 액세스 토큰 전용 응답 쿠키 생성
	 * @param token
	 * @return ResponseCookie
	 */
	public ResponseCookie createAccessTokenCookie(String token) {
		log.info("액세스 토큰 쿠키 생성");
		return ResponseCookie.from(ACCESS_TOKEN.getDescription(), token)
				.httpOnly(true)
				.secure(true)
				.sameSite("none")
				.maxAge(accessTokenExpirationTime / 1000)
				.path("/")
				.build();
	}

	/**
	 * 리프레시 전용 응답 쿠키 생성
	 * @param token
	 * @return ResponseCookie
	 */
	public ResponseCookie createRefreshTokenCookie(String token) {
		log.info("리프레시 토큰 쿠키 생성");
		return ResponseCookie.from(REFRESH_TOKEN.getDescription(), token)
				.httpOnly(true)
				.secure(true)
				.sameSite("none")
				.maxAge(refreshTokenExpirationTime / 1000)
				.path("/")
				.build();
	}

	/**
	 * 액세스 토큰 쿠키 만료
	 * @return ResponseCookie
	 */
	public ResponseCookie expireAccessTokenCookie() {
		log.info("액세스 토큰 쿠키 만료");
		return ResponseCookie.from(ACCESS_TOKEN.getDescription(), "")
				.httpOnly(true)
				.secure(true)
				.sameSite("none")
				.maxAge(0)
				.path("/")
				.build();
	}

	/**
	 * 리프레시 토큰 쿠키 만료
	 * @return ResponseCookie
	 */
	public ResponseCookie expireRefreshTokenCookie() {
		log.info("리프레시 토큰 쿠키 만료");
		return ResponseCookie.from(REFRESH_TOKEN.getDescription(), "")
				.httpOnly(true)
				.secure(true)
				.sameSite("none")
				.maxAge(0)
				.path("/")
				.build();
	}

	/**
	 * 쿠키 값에서 토큰 prefix에 해당하는 토큰 내용만 추출
	 * 블랙리스트 토큰 타입은 처리하지 않음
	 *
	 * @param request HTTP 요청
	 * @param tokenType 토큰 타입
	 * @return 토큰 내용, 없으면 null
	 * @throws IllegalArgumentException 블랙리스트 토큰 타입이 입력된 경우
	 */
	public String extractTokenContent(HttpServletRequest request, TokenType tokenType) {
		if (tokenType.getDescription().startsWith("Blacklist")) {
			throw new IllegalArgumentException("Blacklist token types are not supported");
		}

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				String value = cookie.getValue();
				if (value != null && value.startsWith(tokenType.getPrefix())) {
					return value.substring(tokenType.getPrefix().length());
				}
			}
		}
		return null;
	}
}
