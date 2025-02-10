package com.example.aniwhere.repository.notification;

import com.example.aniwhere.domain.notification.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {

	List<Notification> findAllByMemberIdAndUnread(Long memberId);
	void updateNotificationRead(Long memberId);
}
