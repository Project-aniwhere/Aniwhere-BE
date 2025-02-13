package com.example.aniwhere.repository.notification;

import com.example.aniwhere.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
	List<Notification> findAllByUserIdAndIsReadFalse(Long userId);
}
