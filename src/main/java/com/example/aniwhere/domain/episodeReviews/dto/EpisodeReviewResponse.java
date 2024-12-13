package com.example.aniwhere.domain.episodeReviews.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EpisodeReviewResponse {

	private final Long id;
	private final BigDecimal rating;
	private final String content;
	private final Long userId;

	@QueryProjection
	public EpisodeReviewResponse(Long id, BigDecimal rating, String content, Long userId) {
		this.id = id;
        this.rating = rating;
        this.content = content;
        this.userId = userId;
	}
}
