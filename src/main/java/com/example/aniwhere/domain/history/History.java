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

	@Column(name = "anime_title")
	private String animeTitle;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "approved_at")
	private LocalDateTime approved_at;

	@Column(name = "reply")
	private String reply;

	@Builder
	private History(User sender, User receiver, String animeTitle, Status status) {
		this.sender = sender;
		this.receiver = receiver;
		this.animeTitle = animeTitle;
		this.status = status;
	}

	public void historyUpdate(Status status, String reply) {
		this.reply = reply;
		this.status = status;
	}
}
