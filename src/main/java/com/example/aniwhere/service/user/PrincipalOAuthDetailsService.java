package com.example.aniwhere.service.user;

import com.example.aniwhere.application.config.security.MyUserDetails;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.application.auth.oauth2.KakaoUserInfo;
import com.example.aniwhere.application.auth.oauth2.OAuth2UserInfo;
import com.example.aniwhere.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrincipalOAuthDetailsService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	/**
	 * 소셜 로그인 객체를 조회
	 * @param userRequest
	 * @return OAuth2User
	 * @throws OAuth2AuthenticationException
	 */
	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		return processOAuth2User(userRequest, oAuth2User);
	}

	public OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
		String oAuthAccessToken = request.getAccessToken().getTokenValue();
		KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

		Optional<User> findUser = userRepository.findByEmail(kakaoUserInfo.getEmail());
		User user;

		if (findUser.isPresent()) {
			user = findUser.get();
			updateUserInfo(user, kakaoUserInfo);
		} else {
			user = User.builder()
					.nickname(kakaoUserInfo.getNickname())
					.email(kakaoUserInfo.getEmail())
					.provider(kakaoUserInfo.getProvider())
					.providerId(kakaoUserInfo.getProviderId())
					.role(Role.ROLE_USER)
					.sex(Sex.valueOf(kakaoUserInfo.getSex()))
					.birthyear(kakaoUserInfo.getBirthyear())
					.birthday(kakaoUserInfo.getBirthday())
					.build();
			userRepository.save(user);
		}

		return new MyUserDetails(user, oAuth2User.getAttributes(), oAuthAccessToken);
	}

	/**
	 * 추후 다른 소셜 로그인에 대해 같은 이메일로 로그인을 했다면 일부 필드의 정보들만 업데이트되도록 처리
	 * @param user
	 * @param oAuth2UserInfo
	 * @return User
	 */
	@Transactional
	public User updateUserInfo(User user, OAuth2UserInfo oAuth2UserInfo) {
		return user.updateUser(
				User.builder()
						.nickname(oAuth2UserInfo.getNickname())
						.email(oAuth2UserInfo.getEmail())
						.provider(oAuth2UserInfo.getProvider())
						.providerId(oAuth2UserInfo.getProviderId())
						.build()
		);
	}
}
