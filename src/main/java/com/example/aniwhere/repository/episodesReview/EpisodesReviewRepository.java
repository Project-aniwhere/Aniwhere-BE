package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.domain.episodeReviews.EpisodeReviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodesReviewRepository extends JpaRepository<EpisodeReviews, Long>, EpisodesReviewRepositoryCustom {
}