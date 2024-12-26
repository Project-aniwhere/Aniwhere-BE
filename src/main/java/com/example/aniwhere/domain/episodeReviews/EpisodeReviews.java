package com.example.aniwhere.domain.episodeReviews;

import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

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

	@Column(name = "rating", nullable = false)
	private double rating;

	@Column(name = "content")
	private String content;

	@Builder
	private EpisodeReviews(Episodes episodes, User user, Double rating, String content) {
		this.episodes = episodes;
		this.user = user;
        this.rating = rating;
        this.content = content;

	}

	public void changeRatingAndContent(final double rating, final String content) {
		this.rating = rating;
		this.content = content;
	}
}
