package com.example.aniwhere.domain.episodeReviews.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record EpisodeReviewRequest(
	@Range(min = 0, max = 5, message = "평점은 0~5 사이로 매길 수 있습니다.") double rating,
	@NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.") String content) {

	public static EpisodeReviewRequest of(
			final double rating,
            final String content) {
		return new EpisodeReviewRequest(rating, content);
	}
}
