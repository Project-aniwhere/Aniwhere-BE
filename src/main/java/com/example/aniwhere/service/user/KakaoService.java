package com.example.aniwhere.service.user;

import com.example.aniwhere.application.auth.jwt.dto.Claims;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.OAuthToken;
import com.example.aniwhere.global.error.exception.ExternalServiceException;
import com.example.aniwhere.application.auth.jwt.provider.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.example.aniwhere.global.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

	private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";

	private final TokenProvider tokenProvider;
	private final RedisService redisService;
	private final WebClient webClient;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String url;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	public Mono<OAuthToken> kakaoReissue(String refreshToken) {
		Claims claims = tokenProvider.validateToken(refreshToken);
		String userId = String.valueOf(claims.userId());
		String oAuthRefreshToken = redisService.getOAuthRefreshToken(userId);
		log.info("oAuthRefreshToken: {}", oAuthRefreshToken);

		return webClient.post()
				.uri(url)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("grant_type=refresh_token" +
						"&client_id=" + clientId +
						"&refresh_token=" + oAuthRefreshToken +
						"&client_secret=" + clientSecret)
				.retrieve()
				.bodyToMono(OAuthToken.class)
				.doOnNext(token -> {
					if (token.accessToken() != null && token.refreshToken() != null) {
						redisService.saveOAuthAccessToken(userId, token.accessToken());
						redisService.saveOAuthRefreshToken(userId, token.refreshToken());
					}
				})
				.onErrorMap(WebClientResponseException.class,
						e -> new ExternalServiceException(NETWORK_ERROR, url));
	}

	public Mono<Void> kakaoLogout(String accessToken, String refreshToken) {
		Claims claims = tokenProvider.validateToken(accessToken);
		String userId = String.valueOf(claims.userId());

		return Mono.justOrEmpty(redisService.getOAuthAccessToken(userId))
				.flatMap(oAuthToken -> processKakaoLogout(userId, oAuthToken, accessToken, refreshToken))
				.switchIfEmpty(Mono.defer(() -> {
					handleTokenBlacklisting(userId, accessToken, refreshToken);
					return Mono.empty();
				}))
				.onErrorResume(WebClientResponseException.class, e -> {
					handleTokenBlacklisting(userId, accessToken, refreshToken);
					log.error("Kakao logout failed for userId {}: {}", userId, e.getMessage());
					throw new ExternalServiceException(NETWORK_ERROR, KAKAO_LOGOUT_URL);
				});
	}

	private Mono<Void> processKakaoLogout(String email, String oAuthToken, String accessToken, String refreshToken) {
		return webClient.post()
				.uri(KAKAO_LOGOUT_URL)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + oAuthToken)
				.retrieve()
				.toBodilessEntity()
				.flatMap(response -> {
					return Mono.fromRunnable(() -> {
						redisService.deleteOAuthToken(email);
						redisService.deleteOAuthToken(email);
						handleTokenBlacklisting(email, accessToken, refreshToken);
					});
				})
				.onErrorMap(WebClientResponseException.class,
						e -> new ExternalServiceException(NETWORK_ERROR, KAKAO_LOGOUT_URL, e.getMessage())).then();
	}

	private void handleTokenBlacklisting(String email, String accessToken, String refreshToken) {
		redisService.saveBlackListAccessToken(email, accessToken);
		redisService.saveBlackListRefreshToken(email, refreshToken);
	}
}

