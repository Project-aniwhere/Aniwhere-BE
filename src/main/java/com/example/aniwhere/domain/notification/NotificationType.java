package com.example.aniwhere.domain.notification;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	REQUEST("사용자 요청"),
	RESPONSE("관리자 응답");

	private final String description;
}
