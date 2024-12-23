package com.example.aniwhere.application.auth.oauth2.handler;

import com.example.aniwhere.application.config.cookie.CookieConfig;
import com.example.aniwhere.application.auth.jwt.dto.CreateTokenCommand;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.token.dto.OAuthToken;
import com.example.aniwhere.application.config.security.MyUserDetails;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.application.auth.jwt.provider.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final String FRONT_REDIRECT_URL = "http://localhost:3000";

	private final RedisService redisService;
	private final TokenProvider tokenProvider;
	private final CookieConfig cookieConfig;
	private final OAuth2AuthorizedClientService authorizedClientService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2 Login 성공");
		log.info("SecurityContextHolder.getContext().getAuthentication()={}", SecurityContextHolder.getContext().getAuthentication());

		User user = extractUserInfoFromAuthentication(authentication);
		OAuthToken oAuthToken = extractOAuthTokens(authentication);

		redisService.saveOAuthAccessToken(user.getEmail(), oAuthToken.accessToken());
		redisService.saveOAuthRefreshToken(user.getEmail(), oAuthToken.refreshToken());

		JwtToken jwtToken = generateJwtTokens(user);
		setTokenCookies(response, jwtToken);

		getRedirectStrategy().sendRedirect(request, response, FRONT_REDIRECT_URL);
	}

	private JwtToken generateJwtTokens(User user) {
		CreateTokenCommand command = new CreateTokenCommand(user.getId(), user.getRole());

		String accessToken = tokenProvider.generateAccessToken(command);
		String refreshToken = tokenProvider.generateRefreshToken(command, user);

		log.debug("Generated JWT tokens for user: {}", user.getId());
		return new JwtToken(accessToken, refreshToken);
	}

	private static User extractUserInfoFromAuthentication(Authentication authentication) {
		MyUserDetails details = (MyUserDetails) authentication.getPrincipal();
		return details.getUser();
	}

	private OAuthToken extractOAuthTokens(Authentication authentication) {
		OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
		MyUserDetails details = (MyUserDetails) authentication.getPrincipal();

		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
				oauth2Authentication.getAuthorizedClientRegistrationId(),
				oauth2Authentication.getName()
		);

		return new OAuthToken(
				details.getOAuthAccessToken(),
				client.getRefreshToken().getTokenValue()
		);
	}

	private void setTokenCookies(HttpServletResponse response, JwtToken jwtToken) {
		ResponseCookie accessTokenCookie = cookieConfig.createAccessTokenCookie("access_token", jwtToken.accessToken());
		ResponseCookie refreshTokenCookie = cookieConfig.createRefreshTokenCookie("refresh_token", jwtToken.refreshToken());

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
	}
}
