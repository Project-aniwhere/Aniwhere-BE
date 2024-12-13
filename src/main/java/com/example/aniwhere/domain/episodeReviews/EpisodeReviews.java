package com.example.aniwhere.domain.episodeReviews;

import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "episode_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "episode_review_id"))
public class EpisodeReviews extends Common {

	@ManyToOne
	@JoinColumn(name = "episode_id", nullable = false)
	private Episodes episodes;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "rating", precision = 10, scale = 2)
	private BigDecimal rating;

	@Column(name = "content")
	private String content;

	@Builder
	private EpisodeReviews(Episodes episodes, User user, BigDecimal rating, String content) {
		this.episodes = episodes;
		this.user = user;
        this.rating = rating;
        this.content = content;
	}
}
