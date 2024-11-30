package com.example.aniwhere.service.user.validator;

import com.example.aniwhere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.example.aniwhere.domain.user.dto.UserDTO.*;

@RequiredArgsConstructor
@Component
public class CheckNicknameValidator extends AbstractValidator<UserSignUpRequest> {

	private final UserRepository userRepository;

	@Override
	protected void doValidate(UserSignUpRequest dto, Errors errors) {
		if (userRepository.existsByNickname(dto.getNickname())) {
			errors.rejectValue("nickname", "닉네임 중복 오류", "이미 사용중인 닉네임입니다.");
		}
	}
}
