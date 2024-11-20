package com.example.aniwhere.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(@Email(message = "이메일 형식이 올바르지 않습니다.") String email,
									   @NotBlank(message = "2차 인증 코드는 필수입니다.") String code) {

}
