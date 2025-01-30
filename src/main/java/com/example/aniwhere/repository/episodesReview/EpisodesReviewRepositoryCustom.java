package com.example.aniwhere.repository.episodesReview;

import com.example.aniwhere.application.config.page.PageRequest;
import com.example.aniwhere.application.config.page.PageResponse;
import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodesReviewRepositoryCustom {
	PageResponse<EpisodeReviewResponse> getEpisodeReviews(Long episodeId, PageRequest request);
	PageResponse<EpisodeReviewResponse> getUserEpisodeReviews(String nickname, PageRequest request);
	PageResponse<EpisodeReviewResponse> getMyEpisodeReviews(Long userId, PageRequest request);
}
