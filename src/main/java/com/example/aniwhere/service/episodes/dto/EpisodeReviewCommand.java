package com.example.aniwhere.service.episodes.dto;

import com.example.aniwhere.domain.episodeReviews.dto.EpisodeReviewRequest;

public record EpisodeReviewCommand(Long userId, EpisodeReviewRequest request) {

	public static EpisodeReviewCommand of(final Long userId, final EpisodeReviewRequest request) {
		return new EpisodeReviewCommand(userId, request);
	}
}
