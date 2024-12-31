package com.example.aniwhere.application.auth.kakao;

import com.example.aniwhere.application.auth.kakao.dto.KakaoLogoutResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoProfileResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoTokenResponse;
import com.example.aniwhere.global.error.exception.ServerException;
import com.example.aniwhere.global.error.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.example.aniwhere.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Component
public class WebClientKakaoCaller implements KakaoApi {

	private final WebClient webClient;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	public String redirect_uri;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String client_id;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String client_secret;

	@Override
	public KakaoProfileResponse getProfileInfo(String accessToken) {
		return webClient.get()
				.uri("https://kapi.kakao.com/v2/user/me")
				.headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
						Mono.error(new TokenException(INVALID_TOKEN)))
				.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
						Mono.error(new ServerException(OAUTH2_BAD_GATEWAY_ERROR)))
				.bodyToMono(KakaoProfileResponse.class)
				.block();
	}

	@Override
	public KakaoTokenResponse getAccessToken(String code) {
		return webClient.post()
				.uri("https://kauth.kakao.com/oauth/token")
				.headers(httpHeaders -> {
					httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
					httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				})
				.body(BodyInserters.fromFormData("grant_type", "authorization_code")
						.with("client_id", client_id)
						.with("client_secret", client_secret)
						.with("redirect_uri", redirect_uri)
						.with("code", code))
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
						Mono.error(new TokenException(INVALID_TOKEN)))
				.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
						Mono.error(new ServerException(OAUTH2_BAD_GATEWAY_ERROR)))
				.bodyToMono(KakaoTokenResponse.class)
				.block();
	}

	@Override
	public KakaoLogoutResponse logout(String accessToken) {
		return webClient.post()
				.uri("https://kapi.kakao.com/v1/user/logout")
				.headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
						Mono.error(new TokenException(INVALID_TOKEN)))
				.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
						Mono.error(new ServerException(OAUTH2_BAD_GATEWAY_ERROR)))
				.bodyToMono(KakaoLogoutResponse.class)
				.block();
	}
}
