package com.example.aniwhere.domain.user.dto;

import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import jakarta.validation.constraints.*;
import lombok.*;

public class UserDTO {

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UserSignUpRequest {

		@NotNull(message = "닉네임은 필수입니다.")
		private String nickname;

		@NotNull(message = "이메일은 필수입니다.")
		@Email(message = "이메일 형식이 올바르지 않습니다.")
		private String email;

		@NotNull(message = "비밀번호는 필수 입력 값입니다.")
		private String password;

		@Size(min = 4, max = 4, message = "생년은 4자리여야 합니다.")
		private String birthyear;

		@Size(min = 4, max = 4, message = "출생일자는 4자리여야 합니다.")
		private String birthday;

		@NotNull(message = "2차 인증 코드는 필수입니다.")
		private String authCode;

		@ValidEnum(enumClass = Sex.class)
		private Sex sex;

		private final Role role = Role.ROLE_USER;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UserSignUpResponse {
		private Long id;
		private String nickname;
		private String email;
		private String birthday;
		private String birthyear;
		private Sex sex;
		private Role role;

		public UserSignUpResponse(User user) {
			this.id = user.getId();
			this.email = user.getEmail();
			this.nickname = user.getNickname();
			this.birthday = user.getBirthday();
			this.birthyear = user.getBirthyear();
			this.sex = user.getSex();
			this.role = user.getRole();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UserSignInRequest {

		@Email(message = "이메일 형식이 올바르지 않습니다.")
		private String email;

		@NotNull(message = "비밀번호는 필수 입력 값입니다.")
		private String password;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class EmailVerificationResponse {
		private String message;
		private boolean isVerified;
	}
}