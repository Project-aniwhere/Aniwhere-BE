package com.example.aniwhere.service.user;

import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.dto.OAuthToken;
import com.example.aniwhere.global.error.exception.ExternalServiceException;
import com.example.aniwhere.application.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.example.aniwhere.domain.token.TokenType.*;
import static com.example.aniwhere.global.error.ErrorCode.*;
import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

	private static final String KAKAO_REISSUE_URL = "https://kauth.kakao.com/oauth/token";
	private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";

	private final TokenProvider tokenProvider;
	private final RedisService redisService;
	private final RestTemplate restTemplate;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	/**
	 * 카카오 토큰 재발급 요청 보내기
	 * @param refreshToken
	 * @return OAuthToken
	 */
	public OAuthToken kakaoReissue(String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String email = tokenProvider.getEmail(refreshToken);
		String oAuthRefreshToken = redisService.getOAuthToken(email, OAUTH_REFRESH_TOKEN);
		log.info("oAuthRefreshToken: {}", oAuthRefreshToken);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", clientId);
		params.add("refresh_token", oAuthRefreshToken);
		params.add("client_secret", clientSecret);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<OAuthToken> response = restTemplate.exchange(
					KAKAO_REISSUE_URL,
					POST,
					request,
					OAuthToken.class
			);

			OAuthToken responseBody = response.getBody();
			if (responseBody.accessToken() != null && responseBody.refreshToken() != null) {
				redisService.saveOAuthToken(email, responseBody.accessToken(), OAUTH_ACCESS_TOKEN);
				redisService.saveOAuthToken(email, responseBody.refreshToken(), OAUTH_REFRESH_TOKEN);
			}
			return responseBody;
		} catch (HttpClientErrorException e) {
			throw new ExternalServiceException(NETWORK_ERROR, KAKAO_REISSUE_URL);
		}
 	}

	/**
	 * 카카오 로그아웃 요청 보내기
	 * @param accessToken
	 * @param refreshToken
	 * @return void
	 */
	public void kakaoLogout(String accessToken, String refreshToken) {
		String email = tokenProvider.getEmail(accessToken);

		try {
			Optional.ofNullable(redisService.getOAuthToken(email, ACCESS_TOKEN))
					.ifPresentOrElse(
							oAuthToken -> processKakaoLogout(email, oAuthToken, accessToken, refreshToken),
							() -> handleTokenBlacklisting(email, accessToken, refreshToken)
					);
		} catch (HttpClientErrorException e) {
			handleTokenBlacklisting(email, accessToken, refreshToken);
			throw new ExternalServiceException(NETWORK_ERROR, KAKAO_LOGOUT_URL);
		}
	}

	/**
	 * 카카오 로그아웃 프로세스
	 * @param email
	 * @param oAuthToken
	 * @param accessToken
	 * @param refreshToken
	 */
	private void processKakaoLogout(String email, String oAuthToken,
									String accessToken, String refreshToken) {
		HttpEntity<?> entity = createHttpEntity(oAuthToken);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					KAKAO_LOGOUT_URL,
					POST,
					entity,
					String.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				redisService.deleteOAuthToken(email, OAUTH_ACCESS_TOKEN);
				redisService.deleteOAuthToken(email, OAUTH_REFRESH_TOKEN);
				handleTokenBlacklisting(email, accessToken, refreshToken);
			} else {
				throw new ExternalServiceException(NETWORK_ERROR, KAKAO_LOGOUT_URL, "카카오 로그아웃에 실패했습니다.");
			}
		} catch (HttpClientErrorException e) {
			throw new ExternalServiceException(NETWORK_ERROR, KAKAO_LOGOUT_URL, e.getMessage());
		}
	}

	private HttpEntity<?> createHttpEntity(String oAuthToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(oAuthToken);
		return new HttpEntity<>(headers);
	}

	private void handleTokenBlacklisting(String email, String accessToken, String refreshToken) {
		redisService.saveBlackListJwtToken(email, accessToken, refreshToken);
	}
}
