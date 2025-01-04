package com.example.aniwhere.service.user;

import com.example.aniwhere.application.auth.jwt.dto.CreateTokenCommand;
import com.example.aniwhere.application.auth.jwt.dto.JwtAuthentication;
import com.example.aniwhere.application.auth.kakao.KakaoApi;
import com.example.aniwhere.application.auth.kakao.dto.KakaoLogoutResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoProfileResponse;
import com.example.aniwhere.application.config.cookie.CookieConfig;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.domain.user.dto.UserSignInResult;
import com.example.aniwhere.global.error.exception.UserException;
import com.example.aniwhere.repository.user.UserRepository;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.application.auth.jwt.provider.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.aniwhere.global.error.ErrorCode.*;



@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;
	private final RedisService redisService;
	private final CookieConfig cookieConfig;
	private final KakaoApi kakaoApi;

	public UserSignInResult loginKakaoUser(KakaoProfileResponse profile, String kakaoAccessToken) {

		String email = profile.getKakao_account().getEmail();
		User user;

		if (userRepository.existsByEmail(email)) {
			User existingUser = userRepository.findByEmail(email)
					.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	    	user = existingUser.updateUser(User.builder()
					.providerId("KAKAO_" + email)
					.provider("KAKAO")
					.build());
			log.info("기존 사용자 카카오 정보 업데이트: {}", email);
		} else {
			user = createKakaoUser(profile);
			log.info("신규 카카오 사용자 등록: {}", email);
		}

		JwtToken jwtToken = generateToken(user);

		ResponseCookie accessTokenCookie = cookieConfig.createAccessTokenCookie("access_token", jwtToken.accessToken());
		ResponseCookie refreshTokenCookie = cookieConfig.createRefreshTokenCookie("refresh_token", jwtToken.refreshToken());
		return UserSignInResult.of(user, List.of(accessTokenCookie, refreshTokenCookie));
	}

	public void logoutKakaoUser(HttpServletResponse response) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication.getPrincipal();
		Long userId = jwtAuthentication.userId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		String email = user.getEmail();
		String oAuthAccessToken = redisService.getOAuthAccessToken(email);

		if (oAuthAccessToken != null) {
			KakaoLogoutResponse id = kakaoApi.logout(oAuthAccessToken);
			log.info("카카오 로그아웃 성공 ID: {}", id);

			System.out.println(email);
			redisService.deleteOAuthToken(email);
		}

		cookieConfig.invalidateAuthCookies(response);
	}

	private User createKakaoUser(KakaoProfileResponse profile) {
		String email = profile.getKakao_account().getEmail();
		String gender = profile.getKakao_account().getGender();

		if (userRepository.existsByEmail(profile.getKakao_account().getEmail())) {
			throw new UserException(DUPLICATED_EMAIL);
		}

		User user = User.builder()
				.nickname(profile.getKakao_account().getProfile().getNickname())
				.email(email)
				.provider("KAKAO")
				.providerId("KAKAO_" + email)
				.role(Role.ROLE_USER)
				.sex(Sex.valueOf(gender))
				.birthyear(profile.getKakao_account().getBirthyear())
				.birthday(profile.getKakao_account().getBirthday())
				.build();

		log.info("카카오 회원가입: {}", user.getEmail());
		return userRepository.save(user);
	}

	private JwtToken generateToken(User user) {
		CreateTokenCommand command = new CreateTokenCommand(user.getId(), user.getRole());

		String accessToken = tokenProvider.generateAccessToken(command);
		String refreshToken = tokenProvider.generateRefreshToken(command, user);

		redisService.saveRefreshToken(user.getId(), refreshToken);

		return new JwtToken(accessToken, refreshToken);
	}
}
