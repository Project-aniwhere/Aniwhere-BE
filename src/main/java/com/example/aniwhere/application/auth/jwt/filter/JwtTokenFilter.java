package com.example.aniwhere.application.auth.jwt.filter;

import com.example.aniwhere.application.config.cookie.CookieConfig;
import com.example.aniwhere.application.auth.jwt.JwtWhitelist;
import com.example.aniwhere.application.auth.jwt.provider.JwtAuthenticationProvider;
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
	private final JwtAuthenticationProvider provider;

	public JwtTokenFilter(CookieConfig cookieConfig, JwtAuthenticationProvider provider, JwtWhitelist whitelist) {
		this.whitelist = whitelist;
		this.cookieConfig = cookieConfig;
		this.provider = provider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (whitelist.isWhitelisted(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String accessToken = cookieConfig.extractAccessToken(request);
			log.info("쿠키에서 추출한 액세스 토큰={}", accessToken);

			if (accessToken == null) {
				throw new UserException(UNAUTHORIZED);
			}

			Authentication authentication = provider.authentication(accessToken);
			log.info("생성된 Authentication 객체: principal={}, authorities={}",
					authentication.getPrincipal(),
					authentication.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("Security Context에 인증 정보 저장 완료");
		} catch (Exception e) {
			log.error("토큰 검증 실패", e);
			throw new UserException(UNAUTHORIZED);
		}
		filterChain.doFilter(request, response);
	}
}