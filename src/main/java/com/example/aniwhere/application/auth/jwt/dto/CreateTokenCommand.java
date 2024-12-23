package com.example.aniwhere.application.auth.jwt.dto;

import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.dto.UserDTO;

public record CreateTokenCommand(Long userId, Role role) {

	public static CreateTokenCommand from(UserDTO.UserSignInResponse response) {
		return new CreateTokenCommand(response.getUserId(), response.getRole());
	}

	public static CreateTokenCommand of(final Long userId, final Role role) {
		return new CreateTokenCommand(userId, role);
	}
}
