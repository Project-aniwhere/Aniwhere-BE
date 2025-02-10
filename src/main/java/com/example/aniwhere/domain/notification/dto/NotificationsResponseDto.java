package com.example.aniwhere.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponseDto {

	private List<NotificationDto> notificationDtos;
	private long unreadCount;

	public static NotificationsResponseDto of(List<NotificationDto> notificationDtos, long count) {
		return new NotificationsResponseDto(notificationDtos, count);
	}
}
