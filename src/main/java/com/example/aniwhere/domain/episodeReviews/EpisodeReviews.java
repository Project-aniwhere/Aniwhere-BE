package com.example.aniwhere.domain.episodeReviews;

import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "episode_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeReviews extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "episodeReviews_id")
    private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "episode_id", nullable = false)
	private Episodes episodes;

	@ManyToOne(fetch = LAZY)
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

	public void setEpisodeReview(User user) {
		this.user = user;

		// 무한 루프 방지
		if (!user.getEpisodeReviews().contains(this)) {
			user.getEpisodeReviews().add(this);
		}
	}
}
