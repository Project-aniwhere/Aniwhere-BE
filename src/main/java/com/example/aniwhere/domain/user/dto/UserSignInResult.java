package com.example.aniwhere.domain.user.dto;

import com.example.aniwhere.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignInResult {

	private UserDTO.UserSignInResponse userSignInResponse;
	private List<ResponseCookie> cookies;

	public static UserSignInResult of(User user, List<ResponseCookie> cookies) {
		return new UserSignInResult(
				UserDTO.UserSignInResponse.from(user),
				cookies
		);
	}
}
