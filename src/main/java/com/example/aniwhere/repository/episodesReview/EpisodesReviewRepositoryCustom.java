package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodesReviewRepositoryCustom {
	Page<EpisodeReviewResponse> getEpisodeReviews(Long episodeId, Pageable pageable);
	Page<EpisodeReviewResponse> getUserEpisodeReviews(Long userId, Pageable pageable);
}