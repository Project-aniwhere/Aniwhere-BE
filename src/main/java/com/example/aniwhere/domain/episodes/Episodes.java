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

@Entity
@Getter
@Table(name = "episodes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "episode_id"))
public class Episodes extends Common {

	@ManyToOne
	@JoinColumn(name = "anime_id")
	private Anime anime;

	@OneToMany(mappedBy = "episodes")
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

	@Column(name = "average_rating")
	private double averageRating;

	@Column(name = "episode_story")
	private String episodeStory;

	@Column(name = "still_image")
	private String stillImage;

	public void registerAnime(Anime anime) {
		this.anime = anime;

		// 무한 루프 방지
		if (!anime.getEpisodesList().contains(this)) {
			anime.getEpisodesList().add(this);
		}
	}

	public void updateAverageRating() {
		double newAverage = calculateAverageRating();
		this.averageRating = newAverage;
	}

	private double calculateAverageRating() {
		if (this.episodeReviews == null || this.episodeReviews.isEmpty()) {
			return 0.0;
		}

		return this.episodeReviews.stream()
				.mapToDouble(EpisodeReviews::getRating)
				.average()
				.orElse(0.0);
	}
	public void addEpisodeReview(EpisodeReviews episodeReview) {
		this.episodeReviews.add(episodeReview);
		updateAverageRating();
	}
}
