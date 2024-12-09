package com.example.aniwhere.application.jwt;

import com.example.aniwhere.application.config.CookieConfig;
import com.example.aniwhere.global.error.exception.UserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final static List<String> WHITE_LIST = List.of(
			"/api/auth/login",
			"/api/auth/signup",
			"/api/auth/email/verifications-requests",
			"/api/auth/email/verifications",
			"/api/kakaoreissue",
			"/api/v3/api-docs/**",         // OpenAPI 스펙 문서
			"/api/swagger-ui/**",          // Swagger UI 접근
			"/api/episodes/**",
			"/error",
			"/favicon.ico",
			"/"
	);
	private final CookieConfig cookieConfig;
	private final TokenProvider tokenProvider;

	/**
	 * 백엔드에서 발급해주는 액세스 토큰의 유효성을 검증하여 컨트롤러에 도착하기 전 필터에서 검증
	 * 소셜 로그인 유저에 대해서
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("요청 URI={}", requestURI);

		if (isWhitelisted(requestURI)) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = cookieConfig.resolveAccessTokenInfo(request);
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

	private boolean isWhitelisted(String requestURI) {
		return WHITE_LIST.stream()
				.anyMatch(pattern -> {
					if (pattern.endsWith("/**")) {
						String prefix = pattern.substring(0, pattern.length() - 3);
						return requestURI.startsWith(prefix);
					}
					return pattern.equals(requestURI);
				});
	}
}