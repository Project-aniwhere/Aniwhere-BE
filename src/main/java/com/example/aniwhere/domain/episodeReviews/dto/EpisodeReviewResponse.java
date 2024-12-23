package com.example.aniwhere.domain.episodeReviews.dto;

import com.querydsl.core.annotations.QueryProjection;

public record EpisodeReviewResponse(Long id, Double rating, String content, String nickname) {

	@QueryProjection
	public EpisodeReviewResponse {

	}
}
