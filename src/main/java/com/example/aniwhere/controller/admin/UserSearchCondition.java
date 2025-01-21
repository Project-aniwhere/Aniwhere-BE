package com.example.aniwhere.controller.admin;

import com.example.aniwhere.domain.user.Sex;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSearchCondition {

	private String nickname;
	private String email;
	private Sex sex;

	public UserSearchCondition(String nickname) {
		validateNickname(nickname);
		this.nickname = nickname;
	}

	private void validateNickname(String nickname) {
		if (nickname != null && !StringUtils.hasText(nickname)) {
			throw new IllegalArgumentException("닉네임은 공백일 수 없습니다.");
		}
	}
}
