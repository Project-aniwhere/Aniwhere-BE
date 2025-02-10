package com.example.aniwhere.repository.notification;

import com.example.aniwhere.domain.notification.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.aniwhere.domain.notification.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Notification> findAllByMemberIdAndUnread(Long userId) {
		return queryFactory
				.selectFrom(notification)
				.where(notification.user.id.eq(userId), notification.isRead.eq(false))
				.orderBy(notification.user.id.desc())
				.fetch();
	}

	@Modifying(clearAutomatically = true)
	@Override
	public void updateNotificationRead(Long userId) {
		queryFactory.update(notification)
				.set(notification.isRead, true)
				.where(notification.user.id.eq(userId))
				.execute();
	}
}
