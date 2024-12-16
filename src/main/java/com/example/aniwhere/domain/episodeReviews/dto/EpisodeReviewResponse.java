package com.example.aniwhere.domain.episodeReviews.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EpisodeReviewResponse {

	private final Long id;
	private final Double rating;
	private final String content;
	private final Long userId;

	@QueryProjection
	public EpisodeReviewResponse(Long id, Double rating, String content, Long userId) {
		this.id = id;
        this.rating = rating;
        this.content = content;
        this.userId = userId;
	}
}
