package com.example.aniwhere.application.auth.kakao;

import com.example.aniwhere.application.auth.kakao.dto.KakaoLogoutResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoProfileResponse;
import com.example.aniwhere.application.auth.kakao.dto.KakaoTokenResponse;
import org.springframework.stereotype.Component;

@Component
public interface KakaoApi {

	// 액세스 토큰을 사용해 카카오 프로필 정보 조회
	KakaoProfileResponse getProfileInfo(String accessToken);

	// 인가 코드를 사용해 액세스 토큰 받기
	KakaoTokenResponse getAccessToken(String code);

	// 카카오 로그아웃 호출
	KakaoLogoutResponse logout(String accessToken);
}
