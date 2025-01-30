package com.example.aniwhere.domain.episodes;

import com.example.aniwhere.domain.anime.Anime;
import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "EPISODES")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Episodes extends Common {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "episode_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "anime_id")
	private Anime anime;

	@OneToMany(mappedBy = "episodes", cascade = CascadeType.ALL)
	private List<EpisodeReviews> episodeReviews = new ArrayList<>();

	@Column(name = "episode_number")
	private Integer episodeNumber;

	@Column(name = "title")
	private String title;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "release_date")
	private LocalDate releaseDate;

	@Column(name = "duration")
	private Integer duration;

	@Column(name = "episode_story")
	private String episodeStory;

	@Column(name = "still_image")
	private String stillImage;

	@Column(name = "review_score")
	private double totalReviewScore;

	@Column(name = "review_count")
	private int reviewCount;

	@Column(name = "average_rating")
	private double averageRating;

	public void registerAnime(Anime anime) {
		this.anime = anime;

		// 무한 루프 방지
		if (!anime.getEpisodesList().contains(this)) {
			anime.getEpisodesList().add(this);
		}
	}

	public void addReview(double newRating) {
		this.totalReviewScore += newRating;
		this.reviewCount++;
		updateAverageRating();
	}

	public void updateReview(double oldRating, double newRating) {
		this.totalReviewScore = this.totalReviewScore - oldRating + newRating;
		updateAverageRating();
	}

	public void deleteReview(double oldRating) {
		this.totalReviewScore -= oldRating;
		this.reviewCount--;
		updateAverageRating();
	}

	public void updateAverageRating() {
		this.averageRating = reviewCount > 0 ? this.totalReviewScore / reviewCount : 0.0;
	}
}
