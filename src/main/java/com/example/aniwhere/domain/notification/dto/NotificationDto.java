package com.example.aniwhere.domain.notification.dto;

import com.example.aniwhere.domain.notification.Notification;
import com.example.aniwhere.domain.notification.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDto(

		String message,
		NotificationType notificationType
) {

	public static NotificationDto fromEntity(Notification notification) {
		return NotificationDto.builder()
				.message(notification.getMessage())
				.notificationType(notification.getNotificationType())
				.build();
	}
}