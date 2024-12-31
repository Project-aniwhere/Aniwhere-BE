package com.example.aniwhere.application.auth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoProfileResponse {

	private Long id;
	private KakaoAccount kakao_account;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class KakaoAccount {
		private Profile profile;
		private String email;
		private String birthyear;
		private String birthday;
		private String gender;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Profile {
		private String nickname;
		private String profile_image_url;
	}
}
