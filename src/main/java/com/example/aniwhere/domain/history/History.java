package com.example.aniwhere.domain.history;

import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;

	@Column(name = "content")
	private String content;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ReplyStatus status;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "reply")
	private String reply;

	@Builder
	private History(User sender, User receiver, ReplyStatus status, String content) {
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.content = content;
	}

	// 히스토리 업데이트
	public void historyUpdate(ReplyStatus status, LocalDateTime approvedAt) {
		this.status = status;
		this.approvedAt = LocalDateTime.now();
	}
}
