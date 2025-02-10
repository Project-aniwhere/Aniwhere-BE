package com.example.aniwhere.domain.notification;

import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@Column(name = "message")
	private String message;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Column(name = "is_read")
	@ColumnDefault("false")
	private boolean isRead = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	private Notification(User user, String message, NotificationType notificationType) {
		this.user = user;
		this.message = message;
		this.notificationType = notificationType;
	}

	public void read() {
		this.isRead = true;
	}
}
