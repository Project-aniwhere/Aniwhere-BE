package com.example.aniwhere.domain.episodeReviews.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EpisodeReviewRequest {

	private BigDecimal rating;
	private String content;
	private Long userId;
}
