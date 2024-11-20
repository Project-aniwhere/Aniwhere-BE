package com.example.aniwhere.domain.user.dto;

import jakarta.validation.constraints.Email;

public record EmailAuthenticationRequest(@Email(message = "이메일 형식이 올바르지 않습니다.") String email) {

}
