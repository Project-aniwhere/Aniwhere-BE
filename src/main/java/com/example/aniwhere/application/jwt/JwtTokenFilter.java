package com.example.aniwhere.application.jwt;

import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.global.error.exception.UserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	private final JwtWhitelist whitelist;
	private final CookieConfig cookieConfig;
	private final TokenProvider tokenProvider;

	public JwtTokenFilter(CookieConfig cookieConfig, TokenProvider tokenProvider, JwtWhitelist whitelist) {
		this.whitelist = whitelist;
		this.cookieConfig = cookieConfig;
		this.tokenProvider = tokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("요청 URI={}", requestURI);

		if (whitelist.isWhitelisted(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = cookieConfig.extractAccessToken(request);
		log.info("쿠키에서 추출한 액세스 토큰={}", accessToken);

		if (accessToken == null) {
			throw new UserException(UNAUTHORIZED);
		}

		if (accessToken != null && tokenProvider.validateToken(accessToken)) {
			String email = tokenProvider.getEmail(accessToken);
			log.info("현재 토큰 정보로 조회할 수 있는 이메일={}", email);

			if (email != null) {
				Authentication authentication = tokenProvider.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(request, response);
	}
}