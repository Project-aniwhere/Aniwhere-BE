package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import com.example.aniwhere.domain.episodes.Episodes;
import com.example.aniwhere.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpisodesReviewRepository extends JpaRepository<EpisodeReviews, Long>, EpisodesReviewRepositoryCustom {
	boolean existsByEpisodesAndUser(Episodes episodes, User user);

	Optional<EpisodeReviews> findByEpisodesAndUser(Episodes episodes, User user);
}
